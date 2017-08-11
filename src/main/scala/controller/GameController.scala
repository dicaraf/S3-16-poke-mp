package controller

import java.util.concurrent.{ExecutorService, Executors, Semaphore}
import javax.swing.SwingUtilities

import database.remote.DBConnect
import model.entities._
import model.environment.Direction.Direction
import model.environment.{Audio, Coordinate, CoordinateImpl, Direction}
import model.map.{MainTrainerMovement, Movement}
import utilities.Settings
import view._
import view.dialogue.DialoguePanel
import view.map.GamePanelImpl

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * GameController manages every events on the map (move the trainer, show the game menu, start an interaction, start/stop the game
  * music,... )
  */
trait GameController {

  /**
    * Return the main trainer as a Trainer
    * @return the main trainer
    */
  def trainer: Trainer

  /**
    * Return true if the trainer is moving, false otherwise
    * @return the value that represents if the trainer is moving as a Boolean
    */
  def trainerIsMoving: Boolean

  /**
    * Sets the value that represents if the trainer is moving
    * @param isMoving new value that represents if the trainer is moving
    */
  def trainerIsMoving_=(isMoving: Boolean): Unit

  /**
    * Return true if the trainer is in game, false otherwise
    * @return the value that represents if the trainer is in game as a Boolean
    */
  def isInGame: Boolean

  /**
    * Return true if the trainer is in pause, false otherwise
    * @return the value that represents if the trainer is in pause as a Boolean
    */
  def isInPause: Boolean

  /**
    * Initializes the game, starts the GameControllerAgent, and and accomplishes all things to be done when the game
    * is started
    */
  def start(): Unit

  /**
    * Terminate the game, stops the GameControllerAgent, and and accomplishes all things to be done when the game
    * is terminated
    */
  def terminate(): Unit

  /**
    * Pauses the game, stops the GameControllerAgent, and and accomplishes all things to be done when the game
    * is paused
    */
  def pause(): Unit

  /**
    * Resumes the game from pause, starts the GameControllerAgent, and accomplishes all things to be done when the game
    * is resumed
    */
  def resume(): Unit

  /**
    * Move the trainer in the given direction
    * @param direction the direction towards which the trainer is moving
    */
  def moveTrainer(direction: Direction.Direction): Unit

  /**
    * Permits to interact with other players, npcs, box, ...
    * @param direction the direction towards which the trainer is watching
    */
  def trainerInteract(direction: Direction.Direction): Unit

  /**
    * Shows the game menu
    */
  def showGameMenu(): Unit

  /**
    * Accomplishes the player's logout from the game
    */
  def logout(): Unit

  /**
    * Shows a dialog window
    * @param dialoguePanel the panel to be shown
    */
  def showDialogue(dialoguePanel: DialoguePanel): Unit

  /**
    * Set the current view focusable or not
    */
  def setFocusable(isFocusable: Boolean): Unit

  /**
    * Create a battle with another player's trainer
    * @param otherPlayerId the user id of the other player
    * @param yourPlayerIsFirst boolean that represents if you are the first player to start the battle
    */
  def createTrainersBattle(otherPlayerId: Int, yourPlayerIsFirst: Boolean): Unit

  /**
    * Send to the server the value that represents if you are or not busy
    * @param isBusy value that represents if you are or not busy
    */
  def sendTrainerIsBusy(isBusy: Boolean): Unit
}

/**
  * @inheritdoc
  * @param view instance of the View
  * @param trainer the main trainer
  */
abstract class GameControllerImpl(private var view: View,
                                  override val trainer: Trainer) extends GameController {
  private var agent: GameControllerAgent = _
  private val executor: ExecutorService = Executors.newSingleThreadExecutor()
  private implicit val executionContext = ExecutionContext fromExecutor executor

  protected var inGame = false
  protected var inPause = false
  protected var audio: Audio = _
  protected var gamePanel: GamePanelImpl = _
  protected val waitEndOfMovement: Semaphore = new Semaphore(1)
  protected var trainerMovement: Movement = _
  protected var nextPosition: Coordinate = _

  override var trainerIsMoving: Boolean = false

  /**
    * @inheritdoc
    * @return the value that represents if the trainer is in game as a Boolean
    */
  override def isInGame: Boolean = this.inGame

  /**
    * @inheritdoc
    * @return the value that represents if the trainer is in pause as a Boolean
    */
  override def isInPause: Boolean = this.inPause

  /**
    * @inheritdoc
    */
  override final def start(): Unit = {
    inGame = true
    doStart()
    agent = new GameControllerAgent
    agent.start()
  }

  /**
    * Accomplish the start of the subclass of GameController
    */
  protected def doStart(): Unit

  /**
    * @inheritdoc
    */
  override final def terminate(): Unit = {
    inGame = false
    doTerminate()
    if (agent != null) agent.terminate()
  }

  /**
    * Accomplish the terminate of the subclass of GameController
    */
  protected def doTerminate(): Unit

  /**
    * @inheritdoc
    */
  override final def pause(): Unit = {
    inPause = true
    doPause()
    if (agent != null) agent.terminate()
  }

  /**
    * Accomplish the pause of the subclass of GameController
    */
  protected def doPause(): Unit

  /**
    * @inheritdoc
    */
  override final def resume(): Unit = {
    inPause = false
    doResume()
    agent = new GameControllerAgent
    agent.start()
  }

  /**
    * Accomplish the resume of the subclass of GameController
    */
  protected def doResume(): Unit

  /**
    * @inheritdoc
    * @param direction the direction towards which the trainer is moving
    */
  override final def moveTrainer(direction: Direction): Unit = doMove(direction)

  /**
    * Accomplish the move of the subclass of GameController
    */
  protected def doMove(direction: Direction): Unit

  /**
    * @inheritdoc
    * @param direction the direction towards which the trainer is watching
    */
  override final def trainerInteract(direction: Direction): Unit = doInteract(direction)

  /**
    * Function called when the user wants to interact with someone in front of himself
    * @param direction the direction towards which the trainer is watching
    */
  protected def doInteract(direction: Direction): Unit

  /**
    * @inheritdoc
    */
  override def showGameMenu(): Unit = {
    new GameMenuControllerImpl(view, this)
  }

  /**
    * @inheritdoc
    */
  override def logout(): Unit = {
    DBConnect.closeConnection()
    doLogout()
  }

  /**
    * Accomplish the logout of the subclass of GameController
    */
  protected def doLogout(): Unit

  /**
    * @inheritdoc
    * @param dialoguePanel the panel to be shown
    */
  override def showDialogue(dialoguePanel: DialoguePanel): Unit = {
    setFocusable(false)
    view showDialogue dialoguePanel
  }

  /**
    * @inheritdoc
    */
  override def setFocusable(isFocusable: Boolean): Unit = gamePanel setFocusable isFocusable

  /**
    * Calculate the trainer's position after the movement
    * @param direction direction towards which the trainer is moving
    * @return the next trainer's position as Coordinate
    */
  protected def nextTrainerPosition(direction: Direction): Coordinate = direction match {
    case Direction.UP =>
      trainer.currentSprite = trainer.sprites.backS
      CoordinateImpl(trainer.coordinate.x, trainer.coordinate.y - 1)
    case Direction.DOWN =>
      trainer.currentSprite = trainer.sprites.frontS
      CoordinateImpl(trainer.coordinate.x, trainer.coordinate.y + 1)
    case Direction.RIGHT =>
      trainer.currentSprite = trainer.sprites.rightS
      CoordinateImpl(trainer.coordinate.x + 1, trainer.coordinate.y)
    case Direction.LEFT =>
      trainer.currentSprite = trainer.sprites.leftS
      CoordinateImpl(trainer.coordinate.x - 1, trainer.coordinate.y)
  }

  /**
    * Makes a trainer's walk towards the given direction
    * @param direction the direction towards which the trainer is moving
    * @param nextPosition the final position of the trainer
    */
  protected def walk(direction: Direction, nextPosition: Coordinate): Unit = {
      val movement: Movement = MainTrainerMovement(trainer, gamePanel)
      waitEndOfMovement.acquire()

      val future = Future {
        movement.walk(trainer.coordinate, direction, nextPosition)
      }
      future.onComplete {
        case Success(_) =>
          trainerIsMoving = false
          waitEndOfMovement.release()
        case Failure(e) => e.printStackTrace()
      }
  }

  /**
    * Sets the current trainer's sprite as front
    */
  protected def setTrainerSpriteFront(): Unit = trainer.currentSprite = trainer.sprites.frontS

  /**
    * Sets the current trainer's sprite as back
    */
  protected def setTrainerSpriteBack(): Unit = trainer.currentSprite = trainer.sprites.backS

  /**
    * Thread that refreshes the view every GAME_REFRESH_TIME millis
    */
  private class GameControllerAgent extends Thread {
    private var stopped: Boolean = false

    /**
      * @inheritdoc
      */
    override def run(): Unit = {
      while (isInGame && !stopped) {
        if (!isInPause) {
          try
            SwingUtilities.invokeAndWait(() => gamePanel.repaint())
          catch {
            case e: Exception => println(e)
          }
        }

        try
          Thread sleep Settings.Constants.GAME_REFRESH_TIME
        catch {
          case e: InterruptedException => println(e)
        }
      }
    }

    /**
      * Terminate the thread
      */
    def terminate(): Unit = stopped = true

  }

}
