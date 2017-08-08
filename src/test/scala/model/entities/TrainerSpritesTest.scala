package model.entities

import org.scalatest.FunSuite
import utilities.Settings

class TrainerSpritesTest extends FunSuite{
  def fixture =
    new {
      val trainerSprites: TrainerSprites = TrainerSprites(0)
    }

  test("Sprites with ID 0 should have Trainer1 sprites") {
    val f = fixture
    assert(f.trainerSprites.isInstanceOf[TrainerSprites.Trainer1])
  }

  test("Trainer1 should have TRAINER_1_FRONT_S_IMAGE_STRING sprite") {
    val f = fixture
    assert(f.trainerSprites.frontS.image equals Settings.Images.TRAINER_1_FRONT_S_IMAGE_STRING)
  }

  test("Trainer3 should have ID 2") {
    assert(TrainerSprites.getIdImageFromTrainerSprite(TrainerSprites.Trainer3()) == 2)
  }
}
