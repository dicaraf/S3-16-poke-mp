package distributed.client

import com.google.gson.Gson
import com.rabbitmq.client._
import controller.DistributedBattleController
import distributed.messages.{BattleMessage, BattleMessageImpl}
import utilities.Settings

/**
  * BattleClientManager sends and receives all the messages related to a battle against an other trainer.
  */
trait BattleClientManager {
  /**
    * Send a BattleMessage to the trainer indicated in the arguments
    * @param trainerId id of the trainer whom the message is directed to
    * @param pokemonId id of the current Pokemon fighting (it could be 0 when the fight is finished)
    * @param attackId id of the attack of the fighting Pokemon (it could be 0 if the trainer changes his Pokemon)
    */
  def sendBattleMessage(trainerId: Int, pokemonId: Int, attackId: Int): Unit

  /**
    * Receive and manage every BattleMessage directed to the playing trainer. If the message contains a Pokemon id and
    * an attack id different from 0 it informs the battle controller that an attack from the other Pokemon was received;
    * if the message contains a Pokemon id different from 0 but an attack id equals to 0 it informs the battle
    * controller that the other trainer changed his Pokemon; if the message contains a Pokemon id and an attack id
    * equal to 0 it informs the battle controller that the battle id finished.
    */
  def receiveBattleMessage(): Unit
}

object BattleClientManager {
  def apply(connection: Connection, myPlayerId: Int, otherPlayerId: Int, controller: DistributedBattleController): BattleClientManager =
    new BattleClientManagerImpl(connection, myPlayerId, otherPlayerId, controller)
}

/**
  * @inheritdoc
  * @param connection instance of connection with RabbitMQ
  * @param myPlayerId id of my player
  * @param otherPlayerId if of the other trainer
  * @param controller instance of DistributedBattleController
  */
class BattleClientManagerImpl(private val connection: Connection,
                              private val myPlayerId: Int,
                              private val otherPlayerId: Int,
                              private val controller: DistributedBattleController) extends BattleClientManager {

  private val gson: Gson = new Gson()
  private val channel: Channel = connection.createChannel()
  private val myChannelName: String = Settings.Constants.BATTLE_CHANNEL_QUEUE + myPlayerId
  private val otherChannelName: String = Settings.Constants.BATTLE_CHANNEL_QUEUE + otherPlayerId

  channel.queueDeclare(myChannelName, false, false, false, null)
  channel.queueDeclare(otherChannelName, false, false, false, null)

  /**
    * @inheritdoc
    */
  override def sendBattleMessage(trainerId: Int, pokemonId: Int, attackId: Int): Unit = {
    val battleMessage = BattleMessage(trainerId,pokemonId,attackId)
    channel.basicPublish("", myChannelName, null, gson.toJson(battleMessage).getBytes("UTF-8"))
  }

  /**
    * @inheritdoc
    */
  override def receiveBattleMessage(): Unit = {
    val consumer = new DefaultConsumer(channel) {

      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]) {

        val battleMessage = gson.fromJson(new String(body, "UTF-8"), classOf[BattleMessageImpl])
        if(battleMessage.attackId == 0){
          if(battleMessage.pokemonId == 0){
            closeChannels()
            controller.resumeGame()
          } else {
            controller.otherPokemonChanges(battleMessage.pokemonId)
          }
        } else {
          controller.otherPokemonAttacks(battleMessage.attackId)
        }
      }
    }
    channel.basicConsume(otherChannelName,true,consumer)
  }

  private def closeChannels(): Unit = {
    channel.queueDelete(myChannelName)
    channel.queueDelete(otherChannelName)
    channel.close()
  }
}
