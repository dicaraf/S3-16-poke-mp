package distributed.client

import com.google.gson.Gson
import com.rabbitmq.client._
import distributed.ConnectedPlayers
import distributed.messages.{PlayerInBuildingMessage, PlayerInBuildingMessageImpl}
import utilities.Settings

/**
  * PlayerInBuildingClientManager sends and receives PlayerIsInBuildingMessages and update the related attribute of the player
  * in the map of connected players
  */
trait PlayerInBuildingClientManager {
  /**
    * Sends a PlayerIsInBuildingMessage to the server updating the attribute isVisible of the player
    * @param userId id of the player to update
    * @param isInBuilding boolean that indicates if the player is in building (true) or not (false)
    */
  def sendPlayerIsInBuilding(userId: Int, isInBuilding: Boolean): Unit

  /**
    * Receive a new PlayerIsInBuildingMessage and update the local connected players.
    * @param userId id of the playing trainer
    * @param connectedPlayers players currently connected to the game
    */
  def receiveOtherPlayerIsInBuilding(userId: Int, connectedPlayers: ConnectedPlayers): Unit
}

object PlayerInBuildingClientManager {
  def apply(connection: Connection): PlayerInBuildingClientManager = new PlayerInBuildingClientManagerImpl(connection)
}

/**
  * @inheritdoc
  * @param connection instance of Connection with RabbitMQ
  */
class PlayerInBuildingClientManagerImpl(private val connection: Connection) extends PlayerInBuildingClientManager {

  private val gson: Gson = new Gson()
  private val channel: Channel = connection.createChannel()

  private val playerQueue = channel.queueDeclare.getQueue

  import Settings._
  channel.queueDeclare(Constants.PLAYER_IN_BUILDING_CHANNEL_QUEUE, false, false, false, null)

  channel.exchangeDeclare(Constants.PLAYER_IN_BUILDING_EXCHANGE, "fanout")
  channel.queueBind(playerQueue, Constants.PLAYER_IN_BUILDING_EXCHANGE, "")

  /**
    * @inheritdoc
    * @param userId id of the player to update
    * @param isInBuilding boolean that indicates if the player is in building (true) or not (false)
    */
  override def sendPlayerIsInBuilding(userId: Int, isInBuilding: Boolean): Unit = {
    val playerInBuildingMessage = PlayerInBuildingMessage(userId, isInBuilding)
    channel.basicPublish("", Constants.PLAYER_IN_BUILDING_CHANNEL_QUEUE, null, (gson toJson playerInBuildingMessage).getBytes("UTF-8"))
  }

  /**
    * @inheritdoc
    * @param userId id of the playing trainer
    * @param connectedPlayers players currently connected to the game
    */
  override def receiveOtherPlayerIsInBuilding(userId: Int, connectedPlayers: ConnectedPlayers): Unit = {
    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]) {
        val playerInBuildingMessage = gson.fromJson(new String(body, "UTF-8"), classOf[PlayerInBuildingMessageImpl])

        if (playerInBuildingMessage.userId != userId && (connectedPlayers containsPlayer playerInBuildingMessage.userId))
            connectedPlayers.updateTrainerIsVisible(playerInBuildingMessage.userId, !playerInBuildingMessage.isInBuilding)
      }

    }

    channel.basicConsume(playerQueue, true, consumer)
  }
}
