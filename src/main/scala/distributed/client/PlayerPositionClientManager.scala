package distributed.client

import com.google.gson.{Gson, GsonBuilder}
import com.rabbitmq.client._
import distributed.ConnectedPlayers
import distributed.deserializers.PlayerPositionMessageDeserializer
import distributed.messages.{PlayerPositionMessage, PlayerPositionMessageImpl}
import model.environment.Coordinate
import utilities.Settings

/**
  * PlayerPositionClientManager sends and receives PlayerPositionMessages
  * updating the position of the player in the current connected players
  */
trait PlayerPositionClientManager{
  /**
    * Sends a PlayerPositionMessage to the server when the trainer change position
    * @param userId user id
    * @param position new trainer position
    */
  def sendPlayerPosition(userId: Int, position: Coordinate): Unit

  /**
    * Receives a PlayerPositionMessage when another player changes position
    * updating the player position in the local current connected players
    * @param userId user id
    * @param connectedPlayers players currently connected to the game
    */
  def receiveOtherPlayerPosition(userId: Int, connectedPlayers: ConnectedPlayers): Unit
}

object PlayerPositionClientManager {
  def apply(connection: Connection): PlayerPositionClientManager = new PlayerPositionClientManagerImpl(connection)
}

/**
  * @inheritdoc
  * @param connection instance of Connection with RabbitMQ
  */
class PlayerPositionClientManagerImpl(private val connection: Connection) extends PlayerPositionClientManager{

  private var gson: Gson = new Gson()
  private val channel: Channel = connection.createChannel()

  import Settings._
  channel.queueDeclare(Constants.PLAYER_POSITION_CHANNEL_QUEUE, false, false, false, null)

  /**
    * @inheritdoc
    * @param userId user id
    * @param position new trainer position
    */
  override def sendPlayerPosition(userId: Int, position: Coordinate): Unit = {
    val playerPositionMessage = PlayerPositionMessage(userId, position)
    channel.basicPublish("", Constants.PLAYER_POSITION_CHANNEL_QUEUE, null, (gson toJson playerPositionMessage).getBytes("UTF-8"))
  }

  /**
    * @inheritdoc
    * @param userId user id
    * @param connectedPlayers players currently connected to the game
    */
  override def receiveOtherPlayerPosition(userId: Int, connectedPlayers: ConnectedPlayers): Unit = {

    channel.exchangeDeclare(Constants.PLAYER_POSITION_EXCHANGE, "fanout")
    val playerQueue = channel.queueDeclare.getQueue
    channel.queueBind(playerQueue, Constants.PLAYER_POSITION_EXCHANGE, "")

    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]) {
        val message = new String(body, "UTF-8")
        gson = new GsonBuilder().registerTypeAdapter(classOf[PlayerPositionMessageImpl], PlayerPositionMessageDeserializer).create()
        val otherPlayerPosition = gson.fromJson(message, classOf[PlayerPositionMessageImpl])

        if (otherPlayerPosition.userId != userId && (connectedPlayers containsPlayer otherPlayerPosition.userId))
          connectedPlayers.updateTrainerPosition(otherPlayerPosition.userId, otherPlayerPosition.position)
      }
    }

    channel.basicConsume(playerQueue, true, consumer)
  }
}
