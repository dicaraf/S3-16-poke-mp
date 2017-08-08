package distributed.deserializers

import java.util

import com.google.gson.GsonBuilder
import distributed.{Player, PlayerImpl}
import org.scalatest.FunSuite

class ConnectedPlayersDeserializerTest extends FunSuite {

  test("The ConnectedPlayers map should be correctly got from Json") {
    val userIds = Seq(1, 2, 3)
    val playersMap = createPlayersMap(userIds)

    val gson = new GsonBuilder().registerTypeAdapter(classOf[util.HashMap[Int, Player]], ConnectedPlayersDeserializer).create()
    val playersMapJson = gson toJson playersMap
    val playersMapReturned = gson.fromJson(playersMapJson, classOf[util.HashMap[Int, Player]])

    assert(playersMap == playersMapReturned)
  }

  private def createPlayersMap(userIds: Seq[Int]) = {
    val playersMap = new util.HashMap[Int, Player]()
    userIds foreach (id => playersMap.put(id, PlayerImpl(id,"",1)))
    playersMap
  }
}
