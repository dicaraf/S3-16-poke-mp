package distributed.deserializers

import com.google.gson.{Gson, GsonBuilder}
import distributed.PlayerImpl
import distributed.messages.{PlayerMessage, PlayerMessageImpl}
import org.scalatest.FunSuite

class PlayerMessageDeserializerTest extends FunSuite {

  def fixture =
    new {
      var gson = new Gson()
      val player = PlayerImpl(1, "", 1)
      val playerMessage = PlayerMessage(player)
      val playerMessageJson = gson toJson playerMessage
      gson = new GsonBuilder().registerTypeAdapter(classOf[PlayerMessageImpl], PlayerMessageDeserializer).create()
      val playerMessageReturned = gson.fromJson(playerMessageJson, classOf[PlayerMessageImpl])
    }

  test("The player message should be correctly got from Json") {
    val f = fixture
    assert(f.playerMessage.player == f.playerMessage.player)
  }

}
