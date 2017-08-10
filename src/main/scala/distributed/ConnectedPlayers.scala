package distributed

import java.util
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import model.environment.Coordinate

/**
  * A ConnectedPlayers contains all the current connected players in the game in a map and allows operations on this map.
  */
trait ConnectedPlayers {
  /**
    * Adds a player to the map of connected players
    * @param userId player id to add to the map
    * @param player player to add to the map to the corresponding id
    */
  def add(userId: Int, player: Player): Unit

  /**
    * Gets a player from the map of connected players
    * @param userId player id
    * @return player
    */
  def get(userId: Int): Player

  /**
    * Adds all the players to the map of connected players from another map
    * @param map another map of connected players
    */
  def addAll(map: util.Map[Int, Player]): Unit

  /**
    * Gets all the map of connected players
    * @return the map of connected players
    */
  def getAll: util.Map[Int, Player]

  /**
    * Removes a player from the map of connected players
    * @param userId player id
    */
  def remove(userId: Int): Unit

  /**
    * Checks if a player is present in the map of connected players
    * @param userId player id
    * @return a boolean that indicates if the map contains the player
    */
  def containsPlayer(userId: Int): Boolean

  /**
    * Updates the position of a trainer
    * @param userId trainer id
    * @param position new trainer position
    */
  def updateTrainerPosition(userId: Int, position: Coordinate): Unit

  /**
    * Updates the visibility of a trainer
    * @param userId trainer id
    * @param isVisible trainer visibility
    */
  def updateTrainerIsVisible(userId: Int, isVisible: Boolean): Unit

  /**
    * Updates if trainer a trainer is busy
    * @param userId trainer id
    * @param isBusy true if trainer is busy, false in the other case
    */
  def updateTrainerIsBusy(userId: Int, isBusy: Boolean): Unit

  /**
    * Adds an observer to the map
    * @param observer instance of connected player observer
    */
  def addObserver(observer: ConnectedPlayersObserver): Unit
}

object ConnectedPlayers {
  def apply(): ConnectedPlayers = new ConnectedPlayersImpl()
}

/**
  * @inheritdoc
  */
class ConnectedPlayersImpl extends ConnectedPlayers{
  private val map: ConcurrentMap[Int, Player] = new ConcurrentHashMap[Int, Player]()

  private var observers: List[ConnectedPlayersObserver] = List[ConnectedPlayersObserver]()

  /**
    * @inheritdoc
    * @param userId player id to add to the map
    * @param player player to add to the map to the corresponding id
    */
  override def add(userId: Int, player: Player): Unit = {
    map.put(userId, player)
    observers foreach ( _ newPlayerAdded player)
  }

  /**
    * @inheritdoc
    * @param userId player id
    * @return player
    */
  override def get(userId: Int): Player = map get userId

  /**
    * @inheritdoc
    * @param map another map of connected players
    */
  override def addAll(map: util.Map[Int, Player]): Unit = {
    map forEach (add(_,_))
  }

  /**
    * @inheritdoc
    * @param userId player id
    */
  override def remove(userId: Int): Unit = {
    map remove userId
    observers foreach ( _ playerRemoved userId)
  }

  /**
    * @inheritdoc
    * @return the map of connected players
    */
  override def getAll: util.Map[Int, Player] = map

  /**
    * @inheritdoc
    * @param userId player id
    * @return a boolean that indicates if the map contains the player
    */
  override def containsPlayer(userId: Int): Boolean = map containsKey userId

  /**
    * @inheritdoc
    * @param userId trainer id
    * @param position new trainer position
    */
  override def updateTrainerPosition(userId: Int, position: Coordinate): Unit = {
      get(userId).position = position
      observers foreach( _ playerPositionUpdated userId)
  }

  /**
    * @inheritdoc
    * @param userId    trainer id
    * @param isVisible trainer visibility
    */
  override def updateTrainerIsVisible(userId: Int, isVisible: Boolean): Unit = get(userId).isVisible = isVisible

  /**
    * @inheritdoc
    * @param userId trainer id
    * @param isBusy true if trainer is busy, false in the other case
    */
  override def updateTrainerIsBusy(userId: Int, isBusy: Boolean): Unit = get(userId).isBusy = isBusy

  /**
    * @inheritdoc
    * @param observer instance of connected player observer
    */
  override def addObserver(observer: ConnectedPlayersObserver): Unit = observers = observers :+ observer

}
