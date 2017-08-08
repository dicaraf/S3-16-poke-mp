package distributed.client

import java.util

import com.google.gson.Gson
import com.rabbitmq.client._
import controller.GameController
import distributed.messages.{TrainerDialogueMessage, TrainerDialogueMessageImpl}
import utilities.Settings
import view.dialogue.{ClassicDialoguePanel, TrainerDialoguePanel}

/**
  * TrainerDialogueClientManager manages the delivering and the receiving of messages realated to the request of a new
  * battle and the respective answer.
  */
trait TrainerDialogueClientManager extends ClosableManager{
  /**
    * Return the id of the trainer that sent you a fighting request or to whom you sent a fightin request.
    * @return id of the opposite trainer
    */
  def otherPlayerId: Int

  /**
    * Set the id of the other trainer
    * @param otherPlayerId the id of the other trainer
    */
  def otherPlayerId_=(otherPlayerId: Int): Unit

  /**
    * Send a new TrainerDialogueMessage to the otherPlayerId with the fight request or the answer to a fight request
    * @param otherPlayerId the recipient trainer id
    * @param wantToFight a Boolean signaling if the player sending the message wants to fight or not
    * @param isFirst a Boolean signaling if the player sending the message is requesting to fight or answering to a
    *                fight request
    */
  def sendDialogueRequest(otherPlayerId: Int, wantToFight: Boolean, isFirst: Boolean): Unit

  /**
    * Receive and manage every message received from other trainers. If it receives a message of request to fight, it
    * shows a panel to let the trainer answer to the fight request. If it receives a message of an answer to a fight
    * request it starts a battle against the other trainer (if the answer was positive) or show a panel that inform the
    * trainer that the battle request was rejected.
    */
  def receiveResponse(): Unit

  /**
    * Create a new DistributedBattle through the GameController
    */
  def createBattle(): Unit
}

object TrainerDialogueClientManager {
  def apply(connection: Connection, mapController: GameController): TrainerDialogueClientManager =
    new TrainerDialogueClientManagerImpl(connection, mapController)
}

/**
  * @inheritdoc
  * @param connection instance of Connection to RabbiMQ
  * @param mapController instance of MapController
  */
class TrainerDialogueClientManagerImpl(private val connection: Connection,
                                       private val mapController: GameController) extends TrainerDialogueClientManager {

  private val gson: Gson = new Gson()
  private val channel: Channel = connection.createChannel()
  val playerId: Int = mapController.trainer.id
  override var otherPlayerId: Int = _
  var otherPlayerName: String = _
  private var yourPlayerIsFirst = true

  import Settings._
  private val playerQueue = Constants.TRAINER_DIALOGUE_CHANNEL_QUEUE + playerId

  channel.queueDeclare(playerQueue, false, false, false, null)

  /**
    * @inheritdoc
    */
  override def sendDialogueRequest(otherPlayerId: Int, wantToFight: Boolean, isFirst: Boolean): Unit = {
    mapController.sendTrainerIsBusy(wantToFight)
    yourPlayerIsFirst = isFirst
    val trainerDialogueMessage = TrainerDialogueMessage(playerId, mapController.trainer.username, otherPlayerId, wantToFight, isFirst)
    channel.queueDeclare(Constants.TRAINER_DIALOGUE_CHANNEL_QUEUE + otherPlayerId, false, false, false, null)
    channel.basicPublish("", Constants.TRAINER_DIALOGUE_CHANNEL_QUEUE + otherPlayerId, null, gson.toJson(trainerDialogueMessage).getBytes("UTF-8"))
  }

  /**
    * @inheritdoc
    */
  override def receiveResponse(): Unit = {

    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]) {

        val trainerDialogueMessage = gson.fromJson(new String(body, "UTF-8"), classOf[TrainerDialogueMessageImpl])
        otherPlayerId = trainerDialogueMessage.firstPlayerId
        otherPlayerName = trainerDialogueMessage.firstPlayerName
        if(trainerDialogueMessage.wantToFight && trainerDialogueMessage.isFirst) {
          mapController.sendTrainerIsBusy(true)
          yourPlayerIsFirst = false
          mapController.showDialogue(new TrainerDialoguePanel(mapController, TrainerDialogueClientManagerImpl.this,
            util.Arrays.asList(otherPlayerName + Settings.Strings.WANT_TO_FIGHT)))
        }
        else if(trainerDialogueMessage.wantToFight){
          createBattle()
        }
        else if(!trainerDialogueMessage.wantToFight){
          mapController.sendTrainerIsBusy(false)
          mapController.showDialogue(new ClassicDialoguePanel(mapController, util.Arrays.asList(otherPlayerName +
            Settings.Strings.DONT_WANT_TO_FIGHT)))
        }
      }
    }
    channel.basicConsume(playerQueue, true, consumer)
  }

  /**
    * @inheritdoc
    */
  override def createBattle(): Unit = {
    mapController.createTrainersBattle(otherPlayerId, yourPlayerIsFirst)
  }

  /**
    * @inheritdoc
    */
  override def close(): Unit = {
    channel queueDelete playerQueue
    channel.close()
  }
}
