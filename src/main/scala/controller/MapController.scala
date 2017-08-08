package controller

import java.util

import com.rabbitmq.client.Connection
import database.remote.DBConnect
import distributed.client.{BattleClientManager, BattleClientManagerImpl}
import distributed.{ConnectedPlayers, ConnectedPlayersObserver}
import model.entities.Trainer
import model.environment.Direction.Direction
import model.environment.{Audio, Coordinate, CoordinateImpl}
import model.map._
import utilities.Settings
import view._
import view.dialogue.{ClassicDialoguePanel, WaitingTrainerDialoguePanel}

import scala.util.Random

object MapController {
  private final val RANDOM_MAX_VALUE = 10
  private final val MIN_VALUE_TO_FIND_POKEMON = 8
  private final val POKEMON_CENTER_BUILDING = "Pokemon center"
  private final val LABORATORY_BUILDING = "Laboratory"

  def apply(view: View, _trainer: Trainer, connection: Connection, connectedPlayers: ConnectedPlayers): GameController = new MapController(view, _trainer, connection, connectedPlayers)
}

/**
  * Manages all the events in the map outside buildings (move the trainer on the map, interact with other players,...)
  * @param view instance of the View
  * @param _trainer the main trainer
  * @param connection instance of the connection with RabbitMQ Broker
  * @param connectedPlayers all the players connected to the game
  */
class MapController(private val view: View,
                    private val _trainer: Trainer,
                    private val connection: Connection,
                    private val connectedPlayers: ConnectedPlayers) extends GameControllerImpl(view, _trainer){
  import MapController._

  private val gameMap = MapCreator.create(Settings.Constants.MAP_HEIGHT, Settings.Constants.MAP_WIDTH, InitialTownElements())
  private var lastCoordinates: Coordinate = _
  private val distributedMapController: DistributedMapController = DistributedMapController(this, connection, connectedPlayers)
  connectedPlayers addObserver distributedMapController.asInstanceOf[ConnectedPlayersObserver]
  audio = Audio(Settings.Audio.MAP_SONG)

  /**
    * @inheritdoc
    */
  override protected def doStart(): Unit = {
    initView()
    if(trainer.capturedPokemons.isEmpty){
      doFirstLogin()
    }else {
      audio.loop()
    }
  }

  /**
    * Initializes the panel of the map
    */
  private def initView(): Unit = {
    view.showMap(this, distributedMapController, gameMap)
    gamePanel = view.getGamePanel
  }

  /**
    * Manages the player's first login. In the first login the player must start from the Laboratory
    */
  private def doFirstLogin(): Unit = {
    pause()
    updateLastCoordinateToBuilding(LABORATORY_BUILDING)
    new LaboratoryController(this.view, this, trainer).start()
  }

  /**
    * Sets the trainer's coordinates in front of the door of the given building
    * @param building the building in front of which the coordinates must be set
    */
  private def updateLastCoordinateToBuilding(building: String): Unit = {
    for( x <- 0 until gameMap.width){
      for( y <- 0 until gameMap.height){
        (building, gameMap.map(x)(y)) match {
          case (LABORATORY_BUILDING, tile:Laboratory) =>
            lastCoordinates = CoordinateImpl(tile.topLeftCoordinate.x + tile.doorCoordinates.x, tile.topLeftCoordinate.y + tile.doorCoordinates.y + 1)
            distributedMapController.sendTrainerPosition(lastCoordinates)
            return
          case (POKEMON_CENTER_BUILDING, tile: PokemonCenter) =>
            lastCoordinates = CoordinateImpl(tile.topLeftCoordinate.x + tile.doorCoordinates.x, tile.topLeftCoordinate.y + tile.doorCoordinates.y + 1)
            distributedMapController.sendTrainerPosition(lastCoordinates)
            return
          case _ =>
        }
      }
    }
  }

  /**
    * @inheritdoc
    */
  override protected def doPause(): Unit = {
    lastCoordinates = trainer.coordinate
    audio.pause()
    setFocusableOff()
  }

  /**
    * @inheritdoc
    */
  override protected def doResume(): Unit = {
    distributedMapController sendTrainerInBuilding false
    sendTrainerIsBusy(false)
    checkIfPlayerHasBeenDefeated()
    trainer.coordinate = lastCoordinates
    initView()
    audio.loop()
    setFocusableOn()
  }

  /**
    * Sets the last trainer's coordinates in front of the Pokemon Center door if the trainers has not available Pokemons
    */
  private def checkIfPlayerHasBeenDefeated() = {
    if(trainer.getFirstAvailableFavouritePokemon <= 0) {
      DBConnect rechangeAllTrainerPokemon trainer.id
      setTrainerSpriteFront()
      updateLastCoordinateToBuilding(POKEMON_CENTER_BUILDING)
    }
  }

  /**
    * @inheritdoc
    */
  override protected def doTerminate(): Unit = {
    audio.stop()
  }

  /**
    * @inheritdoc
    * @param direction the direction towards which the trainer is moving
    */
  override protected def doMove(direction: Direction): Unit = {
    if (!isInPause) {
      if(direction != null) nextPosition = nextTrainerPosition(direction)
      val tile = gameMap.map(nextPosition.x)(nextPosition.y)
      tile match {
        case tile:Building
          if nextPosition equals CoordinateImpl(tile.topLeftCoordinate.x + tile.doorCoordinates.x, tile.topLeftCoordinate.y + tile.doorCoordinates.y) =>
          enterInBuilding(tile)
        case _ if tile.walkable =>
          walk(direction, nextPosition)
          distributedMapController sendTrainerPosition nextPosition
          if(tile.isInstanceOf[Tile.TallGrass]) randomPokemonAppearance()
        case _ => trainerIsMoving = false
      }
    }
  }

  /**
    * Moves the trainer into a building
    * @param building the building towards the trainer is moving into
    */
  private def enterInBuilding(building: Building) = {
    distributedMapController sendTrainerInBuilding true
    pause()
    val buildingController: BuildingController = building match{
      case _: PokemonCenter => new PokemonCenterController(this.view, this, trainer)
      case _: Laboratory => new LaboratoryController(this.view, this, trainer)
    }
    buildingController.start()
    trainerIsMoving = false
  }

  /**
    * Checks if a wild Pokemon has been met. If so a battle is started
    */
  private def randomPokemonAppearance() = {
    val random: Int = Random nextInt RANDOM_MAX_VALUE
    if(random >= MIN_VALUE_TO_FIND_POKEMON) {
      sendTrainerIsBusy(true)
      waitEndOfMovement.acquire()
      pause()
      waitEndOfMovement.release()
      new BattleControllerImpl(this, view)
    }
  }

  /**
    * @inheritdoc
    * @param direction the direction towards which the trainer is watching
    */
  override protected def doInteract(direction: Direction): Unit = {
    if (!isInPause){
      if(direction != null) nextPosition = nextTrainerPosition(direction)
      distributedMapController.connectedPlayers.getAll.values() forEach (otherPlayer =>
        if((nextPosition equals otherPlayer.position) &&  !otherPlayer.isBusy){
          distributedMapController.sendChallengeToTrainer(otherPlayer.userId)
          showDialogue(new WaitingTrainerDialoguePanel(otherPlayer.username))
        }else if((nextPosition equals otherPlayer.position) &&  otherPlayer.isBusy){
          showDialogue(new ClassicDialoguePanel(this, util.Arrays.asList(otherPlayer.username + Settings.Strings.BUSY_MESSAGE)))
        })
    }
  }

  /**
    * @inheritdoc
    */
  override protected def doLogout(): Unit = {
    distributedMapController.playerLogout()
    terminate()
  }

  /**
    * @inheritdoc
    * @param otherPlayerId the user id of the other player
    * @param yourPlayerIsFirst boolean that represents if you are the first player to start the battle
    */
  override def createTrainersBattle(otherPlayerId: Int, yourPlayerIsFirst: Boolean): Unit = {
    pause()
    val otherPlayerUsername = (connectedPlayers get otherPlayerId).username
    val distributedBattle: DistributedBattleController = new DistributedBattleControllerImpl(this, view, otherPlayerUsername, yourPlayerIsFirst)
    val battleManager: BattleClientManager = new BattleClientManagerImpl(connection, trainer.id, otherPlayerId, distributedBattle)
    distributedBattle passManager battleManager
  }

  /**
    * @inheritdoc
    * @param isBusy value that represents if you are or not busy
    */
  override def sendTrainerIsBusy(isBusy: Boolean): Unit = {
    distributedMapController sendTrainerIsBusy isBusy
  }
}
