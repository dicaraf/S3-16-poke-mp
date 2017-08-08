package distributed

import java.util

import model.environment.CoordinateImpl
import org.scalatest.FunSuite

class ConnectedPlayersTest extends FunSuite {

  def fixture =
    new {
      val connectedPlayers = ConnectedPlayers()
    }

  test("A player should be added") {
    val f = fixture
    val userId = 1
    val player = createPlayer(userId)
    f.connectedPlayers.add(player.userId,player)
    assert(f.connectedPlayers.containsPlayer(userId))
  }

  test("Given an userId the right player should be got") {
    val f = fixture
    val userId = 1
    val player = createPlayer(userId)
    f.connectedPlayers.add(player.userId,player)
    assert(f.connectedPlayers.get(player.userId) == player)
  }

  test("A map of players should be added") {
    val f = fixture
    val userIds = Seq(1, 2, 3)
    val playersMap = createPlayersMap(userIds)
    f.connectedPlayers addAll playersMap
    assert( f.connectedPlayers.getAll.size() == playersMap.size())
    userIds foreach (id => assert(f.connectedPlayers.get(id) == playersMap.get(id)))

  }

  test("getAll() should return all the elements") {
    val f = fixture
    val userIds = Seq(1, 2, 3)
    val playersMap = createPlayersMap(userIds)
    f.connectedPlayers addAll playersMap
    val playersMapReturned = f.connectedPlayers.getAll
    assert( playersMapReturned.size() == playersMap.size())
    userIds foreach (id => assert(playersMapReturned.get(id) == playersMap.get(id)))
  }

  test("A player should be removed") {
    val f = fixture
    val userIds = Seq(1, 2, 3)
    val playersMap = createPlayersMap(userIds)
    f.connectedPlayers.addAll(playersMap)
    val userIdToRemove = userIds(1)
    f.connectedPlayers.remove(userIdToRemove)
    assert(!f.connectedPlayers.containsPlayer(userIdToRemove))
  }

  test("containsPlayer() should correctly return if a player is present") {
    val f = fixture
    val userIdsPresent = Seq(1, 2, 3)
    val userIdsNotPresent = Seq(4,5,6)
    val playersMap = createPlayersMap(userIdsPresent)
    f.connectedPlayers.addAll(playersMap)
    userIdsPresent foreach (id => assert(f.connectedPlayers.containsPlayer(id)))
    userIdsNotPresent foreach (id => assert(!f.connectedPlayers.containsPlayer(id)))
  }

  test("updateTrainerPosition() should correctly update the trainer position") {
    val f = fixture
    val userId = 1
    val player = createPlayer(userId)
    player.position = (10,10)
    f.connectedPlayers.add(player.userId,player)
    val newPosition = CoordinateImpl(20,20)
    f.connectedPlayers.updateTrainerPosition(userId, newPosition)
    assert(f.connectedPlayers.get(userId).position == newPosition)
  }

  private def createPlayer(userId: Int) = {
    PlayerImpl(userId,"",1)
  }

  private def createPlayersMap(userIds: Seq[Int]) = {
    val playersMap = new util.HashMap[Int, Player]()
    userIds foreach (id => playersMap.put(id, createPlayer(id)))
    playersMap
  }

}
