package controller

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import com.rabbitmq.client.Connection
import distributed.client.{NewPlayerInGameClientManager, PlayerInBuildingClientManager, PlayerLogoutClientManager, PlayerPositionClientManager}
import distributed.{ConnectedPlayers, ConnectedPlayersObserver, Player, PlayerPositionDetails}
import model.entities.TrainerSprites
import model.environment.Direction.Direction
import model.environment.{Coordinate, CoordinateImpl, Direction}
import model.map.{Movement, OtherTrainerMovement}
import utilities.Settings

trait DistributedMapController{
  def connectedPlayers: ConnectedPlayers

  def playersPositionDetails: ConcurrentMap[Int, PlayerPositionDetails]

  def sendTrainerPosition(position: Coordinate): Unit

  def sendTrainerInBuilding(isInBuilding: Boolean): Unit

  def playerLogout(): Unit
}

object DistributedMapController{
  def apply(mapController: GameController, connection: Connection, connectedPlayers: ConnectedPlayers): DistributedMapController =
    new DistributedMapControllerImpl(mapController, connection, connectedPlayers)
}

class DistributedMapControllerImpl(private val mapController: GameController,
                                   private val connection: Connection,
                                   override val connectedPlayers: ConnectedPlayers) extends DistributedMapController with ConnectedPlayersObserver{

  private val trainerId: Int = mapController.trainer.id
  private val newPlayerInGameManager: NewPlayerInGameClientManager = NewPlayerInGameClientManager(connection)
  private val playerPositionManager: PlayerPositionClientManager = PlayerPositionClientManager(connection)
  private val playerInBuildingManager: PlayerInBuildingClientManager = PlayerInBuildingClientManager(connection)
  private val playerLogoutManager: PlayerLogoutClientManager = PlayerLogoutClientManager(connection)

  newPlayerInGameManager.receiveNewPlayerInGame(trainerId, connectedPlayers)
  playerPositionManager.receiveOtherPlayerPosition(trainerId, connectedPlayers)
  playerInBuildingManager.receiveOtherPlayerIsInBuilding(trainerId, connectedPlayers)
  playerLogoutManager.receiveOtherPlayerLogout(trainerId, connectedPlayers)

  override val playersPositionDetails: ConcurrentMap[Int, PlayerPositionDetails] = new ConcurrentHashMap[Int, PlayerPositionDetails]()

  //private val otherTrainerMovement: Movement = OtherTrainerMovement(playersPositionDetails)

  override def sendTrainerPosition(position: Coordinate): Unit = playerPositionManager.sendPlayerPosition(trainerId, position)

  override def sendTrainerInBuilding(isInBuilding: Boolean): Unit = playerInBuildingManager.sendPlayerIsInBuilding(trainerId, isInBuilding)

  override def playerLogout(): Unit = {
    playerLogoutManager.sendPlayerLogout(trainerId)
    connection.close()
  }

  override def newPlayerAdded(player: Player): Unit = {
    val playerDetails = PlayerPositionDetails(player.userId, player.position.x, player.position.y, TrainerSprites.selectTrainerSprite(player.idImage).frontS)
    playersPositionDetails.put(player.userId, playerDetails)
  }

  override def playerPositionUpdated(userId: Int): Unit = {
    val initialPosition = CoordinateImpl(playersPositionDetails.get(userId).coordinateX.asInstanceOf[Int], playersPositionDetails.get(userId).coordinateY.asInstanceOf[Int])
    val nextPosition = connectedPlayers.get(userId).position
    val direction: Direction = (initialPosition, nextPosition) match {
      case (CoordinateImpl(x1, _), CoordinateImpl(x2, _)) if x2 == x1 + 1 => Direction.RIGHT
      case (CoordinateImpl(x1, _), CoordinateImpl(x2, _)) if x2 == x1 - 1 => Direction.LEFT
      case (CoordinateImpl(_, y1), CoordinateImpl(_, y2)) if y2 == y1 + 1 => Direction.DOWN
      case _ => Direction.UP
    }
    //otherTrainerMovement.walk(initialPosition, direction, nextPosition)
  }

  override def playerRemoved(userId: Int): Unit = playersPositionDetails.remove(userId)
//}

/*
class DistributedMapControllerAgent(private val mapController: GameController, private val distributedMapController: DistributedMapController) extends Thread {
  var stopped: Boolean = false

  override def run(): Unit = {
    while(mapController.isInGame && !stopped){
      if(!mapController.isInPause){
       distributedMapController.connectedPlayers.getAll.values() forEach (player => addPlayer(player) )
      }
      try
        Thread.sleep(Settings.GAME_REFRESH_TIME)
      catch {
        case e: InterruptedException => System.out.println(e)
      }
    }
  }

  private def addPlayer(player: Player): Unit = {
    if(distributedMapController.playersPositionDetails.get(player.userId) == null) {
      val playerDetails = PlayerPositionDetails(player.userId, player.position.x, player.position.y, TrainerSprites.selectTrainerSprite(player.idImage).frontS)
      distributedMapController.playersPositionDetails.put(player.userId, playerDetails)
    }
  }

  def terminate(): Unit = {
    stopped = true
  }
*/
}