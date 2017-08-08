package model.map

import javax.swing.JTextField

import database.remote.DBConnect
import model.entities.Trainer
import model.environment.{CoordinateImpl, Direction}
import org.scalatest.FunSuite
import utilities.Settings
import view.map.GamePanel

import scala.collection.mutable

class MainTrainerMovementTest extends FunSuite {

  def fixture =
    new {
      val trainer: Trainer = createNewTrainer()
      val gamePanel = new FakeGamePanel()
      val movement = MainTrainerMovement(trainer, gamePanel)
      val initialPosition = CoordinateImpl(0, 0)
      val nextPosition = CoordinateImpl(1, 0)
      movement.walk(initialPosition, Direction.RIGHT, nextPosition)
    }

  private def createNewTrainer(): Trainer = {
    val map: mutable.Map[String, JTextField] = scala.collection.mutable.Map[String,JTextField]()
    val name = new JTextField("prova")
    val surname = new JTextField("prova")
    val email = new JTextField("prova@prova.it")
    val username = new JTextField("test")
    val password = new JTextField("testtest")
    map += "Name" -> name
    map += "Surname" -> surname
    map += "Email" -> email
    map += "Username" -> username
    map += "Password" -> password
    DBConnect.insertCredentials(collection.JavaConverters.mapAsJavaMap(map),3)
    (DBConnect getTrainerFromDB "test").get()
  }

  test("The current trainer sprite is the one set after a movement") {
    val f = fixture
    assert(f.trainer.currentSprite ==  f.trainer.sprites.rightS)
    DBConnect deleteUserAndRelatedData "test"
  }

  test("The current trainer position is the one set after a movement") {
    val f = fixture
    assert(f.trainer.coordinate == f.nextPosition)
    DBConnect deleteUserAndRelatedData "test"
  }

  test("The current trainer positions in game panel are the one set after a movement") {
    val f = fixture
    assert(f.gamePanel.currentX == 1 * Settings.Constants.TILE_PIXEL)
    assert(f.gamePanel.currentY == 0 * Settings.Constants.TILE_PIXEL)
    DBConnect deleteUserAndRelatedData "test"
  }

  test("The current trainer return in the initial position after two movement (one on the right and one on the left)") {
    val f = fixture
    f.movement.walk(f.nextPosition, Direction.LEFT, f.initialPosition)
    assert(f.trainer.coordinate ==  f.initialPosition)
    assert(f.trainer.currentSprite == f.trainer.sprites.leftS)
    DBConnect deleteUserAndRelatedData "test"
  }

  private[map] class FakeGamePanel extends GamePanel {

    var currentX: Int = 0 * Settings.Constants.TILE_PIXEL
    var currentY: Int = 0 * Settings.Constants.TILE_PIXEL

    /**
      * Updates the current trainer's coordinate x
      *
      * @param x new coordinate x in double
      */
    override def updateCurrentX(x: Double): Unit = currentX = (x * Settings.Constants.TILE_PIXEL).toInt

    /**
      * Updates the current trainer's coordinate y
      *
      * @param y new coordinate y in double
      */
    override def updateCurrentY(y: Double): Unit = currentY = (y * Settings.Constants.TILE_PIXEL).toInt
  }
}