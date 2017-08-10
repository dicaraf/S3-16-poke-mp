package distributed.server

import com.google.gson.Gson
import com.rabbitmq.client._
import distributed.ConnectedPlayers
import distributed.messages.PlayerIsBusyMessageImpl
import utilities.Settings

object PlayerIsBusyServerService {
  def apply(connection: Connection, connectedPlayers: ConnectedPlayers): PlayerIsBusyServerService = new PlayerIsBusyServerService(connection, connectedPlayers)
}

/**
  *
  * @param connection Instance of the connection with RabbitMQ
  * @param connectedPlayers local connected players in the map
  */
class PlayerIsBusyServerService(private val connection: Connection, private val connectedPlayers: ConnectedPlayers) extends CommunicationService{
  override def start(): Unit = {
    val channel: Channel = connection.createChannel

    import Settings._
    channel.queueDeclare(Constants.PLAYER_IS_BUSY_CHANNEL_QUEUE, false, false, false, null)

    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]): Unit = {

        val gson = new Gson()
        val playerIsBusyMessage = gson.fromJson(new String(body, "UTF-8"), classOf[PlayerIsBusyMessageImpl])

        connectedPlayers.updateTrainerIsBusy(playerIsBusyMessage.userId,playerIsBusyMessage.isBusy)

        channel.exchangeDeclare(Constants.PLAYER_IS_BUSY_EXCHANGE, "fanout")
        val response = gson.toJson(playerIsBusyMessage)
        channel.basicPublish(Constants.PLAYER_IS_BUSY_EXCHANGE, "", null, response.getBytes("UTF-8"))
      }
    }

    channel.basicConsume(Constants.PLAYER_IS_BUSY_CHANNEL_QUEUE, true, consumer)
  }
}

