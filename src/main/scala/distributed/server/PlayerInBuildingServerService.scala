package distributed.server

import com.google.gson.Gson
import com.rabbitmq.client._
import distributed.ConnectedPlayers
import distributed.messages.PlayerInBuildingMessageImpl
import utilities.Settings

object PlayerInBuildingServerService {
  def apply(connection: Connection, connectedPlayers: ConnectedPlayers): PlayerInBuildingServerService =
    new PlayerInBuildingServerService(connection, connectedPlayers)
}

/**
  * PlayerInBuildingServerService receives when a player entered/left a building, and sends to all clients that a
  * player entered/left a building
  * @param connection instance of the connection to the RabbitMQ Broker
  * @param connectedPlayers all the players currently connected as ConnectedPlayers
  */
class PlayerInBuildingServerService (private val connection: Connection,
                                     private val connectedPlayers: ConnectedPlayers) extends CommunicationService{
  /**
    * @inheritdoc
    */
  override def start(): Unit = {
    val channel = connection.createChannel

    import Settings._
    channel.queueDeclare(Constants.PLAYER_IN_BUILDING_CHANNEL_QUEUE, false, false, false, null)

    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]): Unit = {
        val gson = new Gson()
        val playerInBuildingMessage = gson.fromJson(new String(body, "UTF-8"), classOf[PlayerInBuildingMessageImpl])

        if (connectedPlayers containsPlayer playerInBuildingMessage.userId) {
         connectedPlayers.updateTrainerIsVisible(playerInBuildingMessage.userId, !playerInBuildingMessage.isInBuilding)


          channel.exchangeDeclare(Constants.PLAYER_IN_BUILDING_EXCHANGE, "fanout")
          val response = gson toJson playerInBuildingMessage
          channel.basicPublish(Constants.PLAYER_IN_BUILDING_EXCHANGE, "", null, response.getBytes("UTF-8"))
        }
      }
    }

    channel.basicConsume(Constants.PLAYER_IN_BUILDING_CHANNEL_QUEUE, true, consumer)
  }
}
