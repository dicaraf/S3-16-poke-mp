package model.map

import java.util.concurrent.ConcurrentHashMap

import distributed.PlayerPositionDetails
import model.entities.TrainerSprites
import model.environment.{CoordinateImpl, Direction}
import org.scalatest.FunSuite

class OtherTrainerMovementTest extends FunSuite {

  def fixture =
    new {
      val playerPositionDetails = PlayerPositionDetails(1, 0, 0, TrainerSprites(1).frontS)
      val playersPositionDetails = new ConcurrentHashMap[Int, PlayerPositionDetails]()
      playersPositionDetails.put(1, playerPositionDetails)
      val movement = OtherTrainerMovement(1, playersPositionDetails, TrainerSprites(1))
      val initialPosition = CoordinateImpl(0, 0)
      val nextPosition = CoordinateImpl(1, 0)
      movement.walk(initialPosition, Direction.RIGHT, nextPosition)
    }

  test("The current trainer sprite is the one set after a movement") {
    val f = fixture
    assert(f.playerPositionDetails.currentSprite ==  TrainerSprites(1).rightS)
  }

  test("The current trainer position is the one set after a movement") {
    val f = fixture
    assert(f.playerPositionDetails.coordinateX == f.nextPosition.x)
    assert(f.playerPositionDetails.coordinateY == f.nextPosition.y)
    assert((f.playersPositionDetails get 1).coordinateX == f.nextPosition.x)
    assert((f.playersPositionDetails get 1).coordinateY == f.nextPosition.y)
  }

  test("The current trainer return in the initial position after two movement (one on the right and one on the left)") {
    val f = fixture
    f.movement.walk(f.nextPosition, Direction.LEFT, f.initialPosition)
    assert((f.playersPositionDetails get 1).coordinateX == f.initialPosition.x)
    assert((f.playersPositionDetails get 1).coordinateY == f.initialPosition.y)
    assert(f.playerPositionDetails.currentSprite ==  TrainerSprites(1).leftS)
  }

}
