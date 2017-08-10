package distributed.client

import com.google.gson.Gson
import com.rabbitmq.client._
import distributed.ConnectedPlayers
import distributed.messages.{PlayerIsBusyMessage, PlayerIsBusyMessageImpl}
import utilities.Settings


/**
  * PlayerIsBusyClientManager sends and receives PlayerIsBusyMessages and update the related attribute for the
  * local variable of the connected user.
  */
trait PlayerIsBusyClientManager {
  /**
    * Send a new message updating the attribute isFighting for a specific user id.
    * @param userId id of the user to update
    * @param isBusy boolean that indicates if the player is busy (true) or not (false)
    */
  def sendPlayerIsBusy(userId: Int, isBusy: Boolean): Unit

  /**
    * Receive a new PlayerIsBusyMessage and update the local connected players.
    * @param userId id of the playing trainer
    * @param connectedPlayers connected players in the current map
    */
  def receiveOtherPlayerIsBusy(userId: Int, connectedPlayers: ConnectedPlayers): Unit
}

object PlayerIsBusyClientManager {
  def apply(connection: Connection): PlayerIsBusyClientManager = new PlayerIsBusyClientManagerImpl(connection)
}

/**
  * @inheritdoc
  * @param connection instance of Connection with RabbitMQ
  */
class PlayerIsBusyClientManagerImpl(private val connection: Connection) extends PlayerIsBusyClientManager {

  private val gson: Gson = new Gson()
  private val channel: Channel = connection.createChannel()

  private val playerQueue = channel.queueDeclare.getQueue

  import Settings._
  channel.queueDeclare(Constants.PLAYER_IS_BUSY_CHANNEL_QUEUE, false, false, false, null)

  channel.exchangeDeclare(Constants.PLAYER_IS_BUSY_EXCHANGE, "fanout")
  channel.queueBind(playerQueue, Constants.PLAYER_IS_BUSY_EXCHANGE, "")

  /**
    * @inheritdoc
    */
  override def sendPlayerIsBusy(userId: Int, isBusy: Boolean): Unit = {
    val playerIsBusyMessage = PlayerIsBusyMessage(userId, isBusy)
    channel.basicPublish("", Constants.PLAYER_IS_BUSY_CHANNEL_QUEUE, null, (gson toJson playerIsBusyMessage).getBytes("UTF-8"))
  }

  /**
    * @inheritdoc
    */
  override def receiveOtherPlayerIsBusy(userId: Int, connectedPlayers: ConnectedPlayers): Unit = {
    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]) {
        val playerIsBusyMessage = gson.fromJson(new String(body, "UTF-8"), classOf[PlayerIsBusyMessageImpl])

        if (playerIsBusyMessage.userId != userId && connectedPlayers.containsPlayer(playerIsBusyMessage.userId))
          connectedPlayers.updateTrainerIsBusy(playerIsBusyMessage.userId, playerIsBusyMessage.isBusy)
      }

    }

    channel.basicConsume(playerQueue, true, consumer)
  }
}
