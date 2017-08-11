package controller

import model.entities.{OakAfterChoise, Trainer}
import model.environment.Direction.Direction
import model.environment._
import model.map._
import utilities.Settings
import view._
import view.dialogue.{ClassicDialoguePanel, DoctorDialoguePanel}

import scala.collection.JavaConverters._

/**
  * Manages all the events inside buildings (move the trainer in the buildings, interact with elements, ...)
  * @param view instance of the View
  * @param mapController instance of  the map controller
  * @param _trainer the main trainer
  */
abstract class BuildingController(private val view: View,
                                  private val mapController: GameController,
                                  private val _trainer: Trainer) extends GameControllerImpl(view, _trainer) {

  protected var buildingMap: BuildingMap

  this.setTrainerSpriteBack()

  /**
    * @inheritdoc
    * @param direction the direction towards which the trainer is moving
    */
  override protected def doMove(direction: Direction): Unit = {
    if (!isInPause) {
      nextPosition = nextTrainerPosition(direction)
      try {
        val tile = buildingMap.map(nextPosition.x)(nextPosition.y)
        tile match {
          case _ if tile.walkable =>
            walk(direction, nextPosition)
          case _ => trainerIsMoving = false
        }
      } catch {
        case _: ArrayIndexOutOfBoundsException =>
          trainerIsMoving = false
          if (trainer.coordinate equals buildingMap.entryCoordinate) {
            if(buildingMap.isInstanceOf[LaboratoryMap] && trainer.capturedPokemons.isEmpty){
              showDialogue(new ClassicDialoguePanel(this, buildingMap.staticCharacter.dialogue.asJava))
            }else {
              this.terminate()
              mapController.resume()
            }
          }
        case _: NullPointerException => trainerIsMoving = false
      }
    }
  }

  /**
    * @inheritdoc
    */
  override protected def doStart(): Unit = {
    audio.loop()
  }

  /**
    * @inheritdoc
    */
  override protected def doTerminate(): Unit = {
    audio.stop()
  }

  /**
    * @inheritdoc
    */
  override protected def doPause(): Unit = {
    setFocusable(false)
    this.audio.pause()
  }

  /**
    * @inheritdoc
    */
  override protected def doResume(): Unit = {
    setFocusable(true)
    this.audio.loop()
  }

  /**
    * @inheritdoc
    */
  override protected def doLogout(): Unit = {
    terminate()
    mapController.logout()
  }

  /**
    * @inheritdoc
    * @param otherPlayerId the user id of the other player
    * @param yourPlayerIsFirst boolean that represents if you are the first player to start the battle
    */
  override def createTrainersBattle(otherPlayerId: Int, yourPlayerIsFirst: Boolean): Unit = {}

  /**
    * @inheritdoc
    * @param isBusy value that represents if you are or not busy
    */
  override def sendTrainerIsBusy(isBusy: Boolean): Unit = {}

}

/**
  * Manages all the events inside the pokémon center (building)
  * @param view instance of the View
  * @param mapController instance of  the map controller
  * @param _trainer the main trainer
  */
class PokemonCenterController(private val view: View,
                              private val mapController: GameController,
                              private val _trainer: Trainer) extends BuildingController(view, mapController, _trainer){
  override protected var buildingMap: BuildingMap = new PokemonCenterMap
  this.trainer.coordinate = CoordinateImpl(buildingMap.entryCoordinate.x, buildingMap.entryCoordinate.y)

  audio = Audio(Settings.Audio.POKEMONCENTER_SONG)

  /**
    * @inheritdoc
    */
  override protected def doStart(): Unit = {
    super.doStart()
    initView()
  }

  /**
    * @inheritdoc
    */
  override protected def doResume(): Unit = {
    super.doResume()
    initView()
  }

  /**
    * Show the PokemonCenterPanel
    */
  private def initView(): Unit = {
    view.showPokemonCenter(this, buildingMap)
    gamePanel = view.getGamePanel
  }

  /**
    * In the laboratory, the user can interact with the Doctor and the Box
    * @param direction the direction towards which the trainer is watching
    */
  override protected def doInteract(direction: Direction): Unit = {
    if (!isInPause) {
      if(direction != null) nextPosition = nextTrainerPosition(direction)
      try{
        val tile = buildingMap.map(nextPosition.x)(nextPosition.y)
        if(nextPosition equals buildingMap.staticCharacter.coordinate){
          showDialogue(new DoctorDialoguePanel(this, buildingMap.staticCharacter.dialogue.asJava))
        }
        if(tile.isInstanceOf[Tile.Box]){
          this.pause()
          view showBoxPanel this
        }
      }catch{
        case e: ArrayIndexOutOfBoundsException => e.printStackTrace()
      }
    }
  }
}

/**
  * Manages all the events inside the laboratory (building)
  * @param view instance of the View
  * @param mapController instance of  the map controller
  * @param _trainer the main trainer
  */
class LaboratoryController(private val view: View,
                           private val mapController: GameController,
                           private val _trainer: Trainer) extends BuildingController(view, mapController, _trainer){
  override protected var buildingMap: BuildingMap = new LaboratoryMap
  private var capturedPokemonEmpty: Boolean = this.trainer.capturedPokemons.isEmpty
  if(!capturedPokemonEmpty) buildingMap.staticCharacter = new OakAfterChoise
  this.trainer.coordinate = CoordinateImpl(buildingMap.entryCoordinate.x, buildingMap.entryCoordinate.y)

  audio = Audio(Settings.Audio.LABORATORY_SONG)

  /**
    * @inheritdoc
    */
  override protected def doStart(): Unit = {
    super.doStart()
    initView()
  }

  /**
    * @inheritdoc
    */
  override protected def doResume(): Unit = {
    super.doResume()
    capturedPokemonEmpty = this.trainer.capturedPokemons.isEmpty
    initView()
  }

  /**
    * Show the LaboratoryPanel
    */
  private def initView(): Unit = {
    view.showLaboratory(this, buildingMap, this.trainer.capturedPokemons.isEmpty)
    gamePanel = view.getGamePanel
  }

  /**
    * In the laboratory, the user can interact with Oak and PokemonCharacter
    * @param direction the direction towards which the trainer is watching
    */
  override protected def doInteract(direction: Direction): Unit = {
    if (!isInPause) {
      if(direction != null) nextPosition = nextTrainerPosition(direction)
      try{
        if(nextPosition equals buildingMap.staticCharacter.coordinate) {
          showDialogue(new ClassicDialoguePanel(this, buildingMap.staticCharacter.dialogue.asJava))
        }
        if(capturedPokemonEmpty) {
          for (pokemon <- buildingMap.pokemonCharacter) if (nextPosition equals pokemon.coordinate) {
            this.pause()
            this.view.showInitialPokemonPanel(this, pokemon.pokemonWithLife)
          }
        }
      }catch{
        case e: ArrayIndexOutOfBoundsException => e.printStackTrace()
      }
    }
  }
}

