package model.map

import model.entities.{Sprite, Trainer, TrainerSprites}
import model.environment.{Coordinate, CoordinateImpl}
import view.map.GamePanel

/**
  * MainTrainerMovement represent the movement of the main trainer in the map
  * @param trainer main trainer
  * @param gamePanel instance of game panel
  */
case class MainTrainerMovement(private val trainer: Trainer,
                               private val gamePanel: GamePanel) extends MovementImpl{
  /**
    * @inheritdoc
    * @return the current trainer sprite
    */
  override protected def currentTrainerSprite: Sprite = trainer.currentSprite

  /**
    * @inheritdoc
    * @param sprite trainer sprite
    */
  override protected def currentTrainerSprite_=(sprite: Sprite): Unit = trainer.currentSprite = sprite

  override protected val trainerSprites: TrainerSprites = trainer.sprites

  /**
    * @inheritdoc
    * @param actualX new x coordinate
    */
  override protected def updateCurrentX(actualX: Double): Unit = gamePanel.updateCurrentX(actualX)

  /**
    * @inheritdoc
    * @param actualY new y coordinate
    */
  override protected def updateCurrentY(actualY: Double): Unit = gamePanel.updateCurrentY(actualY)

  /**
    * @inheritdoc
    * @param nextPosition next trainer position
    */
  override protected def updateTrainerPosition(nextPosition: Coordinate): Unit = trainer.coordinate = CoordinateImpl(nextPosition.x, nextPosition.y)

}
