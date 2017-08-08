package distributed.deserializers

import com.google.gson.{Gson, GsonBuilder}
import distributed.messages.{ConnectedPlayersMessage, ConnectedPlayersMessageImpl}
import distributed.{ConnectedPlayers, PlayerImpl}
import org.scalatest.FunSuite

class ConnectedPlayerMessageDeserializerTest extends FunSuite {

  def fixture =
    new {
      var gson = new Gson()
      val connectedPlayers = ConnectedPlayers()
      connectedPlayers.add(1, PlayerImpl(1, "", 1))
      val connectedPlayersMessage = ConnectedPlayersMessage(connectedPlayers.getAll)
      val connectedPlayersMessageJson = gson toJson connectedPlayersMessage

      gson = new GsonBuilder().registerTypeAdapter(classOf[ConnectedPlayersMessageImpl], ConnectedPlayersMessageDeserializer).create()
      val connectedPlayersMessageReturned = gson.fromJson(connectedPlayersMessageJson, classOf[ConnectedPlayersMessageImpl])
    }

  test("The connected players message should be correctly got from Json") {
    val f = fixture
    assert(f.connectedPlayersMessage.connectedPlayers == f.connectedPlayersMessageReturned.connectedPlayers)
  }
}
