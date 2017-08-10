package model.map

import model.environment.{Coordinate, CoordinateImpl}

/**
  * @inheritdoc
  * GameMap represents the map of the game composed by tiles that represent elements of the map
  */
trait GameMap extends BasicMap{
  /**
    * Adds a tile in the indicated coordinate
    * @param coordinate coordinate where add a tile
    * @param tile tile to add to the game map
    */
  def addTile(coordinate: Coordinate, tile: Tile): Unit
}

object GameMap {
  def apply(height: Int, width: Int): GameMap = new GameMapImpl(height: Int, width)
}

/**
  * @inheritdoc
  * @param height height of the game map
  * @param width width of the game map
  */
class GameMapImpl(override val height: Int,
                  override val width: Int) extends GameMap{

  override val map = Array.ofDim[Tile](height,width)
  initMap()

  /**
    * Initializes the game map with all grass
    */
  private def initMap(): Unit = {
    for( x <- 0 until width)
      for( y <- 0 until height)
        map(x)(y) = Tile.Grass()
  }

  /**
    * @inheritdoc
    * Adds a tile in the indicated coordinate, or more equal tile if a tile is larger than 1
    * @param coordinate coordinate where add a tile
    * @param tile tile to add to the game map
    */
  override def addTile(coordinate: Coordinate, tile: Tile): Unit = coordinate match {
    case CoordinateImpl(x,y) if ( x >= 0 && y >= 0 ) && ( x + tile.width <= width && y + tile.height <= height) =>
      for (x <- coordinate.x until coordinate.x + tile.width)
        for (y <- coordinate.y until coordinate.y + tile.height)
          map(x)(y) = tile
    case _ => throw WrongPositionException()
  }

}

