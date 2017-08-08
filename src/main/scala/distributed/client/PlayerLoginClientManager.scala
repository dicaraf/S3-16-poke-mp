package distributed.client

import com.google.gson.{Gson, GsonBuilder}
import com.rabbitmq.client._
import distributed._
import distributed.deserializers.ConnectedPlayersMessageDeserializer
import distributed.messages.{ConnectedPlayersMessageImpl, PlayerMessage}
import utilities.Settings

/**
  * PlayerLoginClientManager sends a message when a player logs in to the game and receives a message
  * with all the player present in the game update the current connected players
  */
trait PlayerLoginClientManager{
  /**
    * Sends a PlayerMessage to the server when the player logs in to the game
    * @param player player to add to the server connected players
    */
  def sendPlayer(player: Player): Unit

  /**
    * Receives all the other player in game in a ConnectedPlayersMessage updating the local current connected players
    * @param userId user id
    * @param connectedPlayers players currently connected to the game (initially empty)
    */
  def receivePlayersConnected(userId: Int, connectedPlayers: ConnectedPlayers): Unit
}

object PlayerLoginClientManager {
  def apply(connection: Connection): PlayerLoginClientManager = new PlayerLoginClientManagerImpl(connection)
}

/**
  * @inheritdoc
  * @param connection instance of Connection with RabbitMQ
  */
class PlayerLoginClientManagerImpl(private val connection: Connection) extends PlayerLoginClientManager {

  private var gson: Gson = new Gson()
  private val channel: Channel = connection.createChannel()

  import Settings._
  channel.queueDeclare(Constants.PLAYER_LOGIN_CHANNEL_QUEUE, false, false, false, null)

  /**
    * @inheritdoc
    * @param player player to add to the server connected players
    */
  override def sendPlayer(player: Player): Unit = {
    val playerMessage = PlayerMessage(player)
    channel.basicPublish("", Constants.PLAYER_LOGIN_CHANNEL_QUEUE, null, (gson toJson playerMessage).getBytes("UTF-8"))
  }

  /**
    * @inheritdoc
    * @param userId user id
    * @param connectedPlayers players currently connected to the game (initially empty)
    */
  override def receivePlayersConnected(userId: Int, connectedPlayers: ConnectedPlayers): Unit = {
    val playerQueue = Constants.PLAYERS_CONNECTED_CHANNEL_QUEUE + userId
    channel.queueDeclare(playerQueue, false, false, false, null)

    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]) {
        val message = new String(body, "UTF-8")
        gson = new GsonBuilder().registerTypeAdapter(classOf[ConnectedPlayersMessageImpl], ConnectedPlayersMessageDeserializer).create()
        val serverPlayersMessage = gson.fromJson(message, classOf[ConnectedPlayersMessageImpl])
        connectedPlayers addAll serverPlayersMessage.connectedPlayers

        channel queueDelete playerQueue
        channel.close()
      }
    }
    channel.basicConsume(playerQueue, true, consumer)
  }

}
