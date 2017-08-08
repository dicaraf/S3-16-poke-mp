package distributed.deserializers

import com.google.gson.{Gson, GsonBuilder}
import distributed.messages.{PlayerPositionMessage, PlayerPositionMessageImpl}
import model.environment.CoordinateImpl
import org.scalatest.FunSuite

class PlayerPositionMessageDeserializerTest extends FunSuite {

  def fixture =
    new {
      var gson = new Gson()
      val positionMessage = PlayerPositionMessage(0, CoordinateImpl(0, 0))
      val positionMessageJson = gson toJson positionMessage
      gson = new GsonBuilder().registerTypeAdapter(classOf[PlayerPositionMessageImpl], PlayerPositionMessageDeserializer).create()
      val positionMessageReturned = gson.fromJson(positionMessageJson, classOf[PlayerPositionMessageImpl])
    }

  test("The player position message should be correctly got from Json") {
    val f = fixture
    assert(f.positionMessage.position == f.positionMessageReturned.position &&
      f.positionMessage.userId == f.positionMessageReturned.userId)
  }

}
