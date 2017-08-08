package distributed.deserializers

import com.google.gson.{Gson, GsonBuilder}
import distributed.PlayerImpl
import org.scalatest.FunSuite

class PlayerDeserializerTest extends FunSuite {

  def fixture =
    new {
      var gson = new Gson()
      val player = PlayerImpl(1, "", 1)
      val playerJson = gson toJson player

      gson = new GsonBuilder().registerTypeAdapter(classOf[PlayerImpl], PlayerDeserializer).create()
      val playerReturned = gson.fromJson(playerJson, classOf[PlayerImpl])
    }

  test("The player should be correctly got from Json") {
    val f = fixture
    assert(f.player.idImage == f.playerReturned.idImage &&
      f.player.isBusy == f.playerReturned.isBusy &&
      f.player.isVisible == f.playerReturned.isVisible &&
      f.player.userId == f.playerReturned.userId &&
      f.player.position == f.playerReturned.position &&
      f.player.username == f.playerReturned.username)
  }

}
