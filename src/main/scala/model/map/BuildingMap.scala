package model.map

import java.awt.Image

import model.entities._
import model.environment.{Coordinate, CoordinateImpl}
import utilities.Settings
import view.LoadImage
import Tile._

/**
  * BuildingMap represents the content of a building map
  */
trait BuildingMap extends BasicMap {
  /**
    * @return background image of a building map
    */
  def image: Image

  /**
    * Some tile in a building map must be not walkable for the trainer
    * @return a list of not walkable coordinate rectangles
    */
  def matricesNotWalkable: List[Tuple2[Coordinate, Coordinate]]

  /**
    * @return the static character in the building map
    */
  def staticCharacter: StaticCharacter

  /**
    * Setter for staticCharacter
    * @param staticCharacter
    */
  def staticCharacter_=(staticCharacter: StaticCharacter): Unit

  /**
    * @return the coordinate of the entry of the building map
    */
  def entryCoordinate: Coordinate

  /**
    * @return a list of pokemon character
    */
  def pokemonCharacter: List[PokemonCharacter]

  /**
    * It sets BasicTile() for every tile in the building map
    */
  protected def setBasicTilesInMap(): Unit = {
    for(i <- map.indices){
      for(j <- map(0).indices){
        map(i)(j) = BasicTile()
      }
    }
  }

  /**
    * It allows to set some tile not walkable (Barrier()) of the building map
    */
  protected def setNotWalkableArea(): Unit = {
    for(matrixNotWalkable <- matricesNotWalkable){
      for( i <- matrixNotWalkable._1.x to matrixNotWalkable._2.x){
        for( j <- matrixNotWalkable._1.y to matrixNotWalkable._2.y){
          map(i)(j) = Barrier()
        }
      }
    }
    map(staticCharacter.coordinate.x)(staticCharacter.coordinate.y) = Barrier()
  }
}

/**
  * PokemonCenterMap is the BuildingMap for the Pokémon Center
  */
class PokemonCenterMap extends BuildingMap {
  override def height: Int = Settings.Constants.POKEMON_CENTER_INSIDE_HEIGHT
  override def width: Int = Settings.Constants.POKEMON_CENTER_INSIDE_WIDTH
  override val map: Array[Array[Tile]]= Array.ofDim[Tile](width, height)

  override var staticCharacter: StaticCharacter = new Doctor

  override val image: Image = LoadImage.load(Settings.Images.MAP_IMAGES_FOLDER + "pokemon-center.png")

  override val matricesNotWalkable: List[Tuple2[Coordinate, Coordinate]] =
    List(Tuple2(CoordinateImpl(0,0),CoordinateImpl(14,1)),
         Tuple2(CoordinateImpl(4,2),CoordinateImpl(10,3)),
         Tuple2(CoordinateImpl(0,5),CoordinateImpl(1,6)),
         Tuple2(CoordinateImpl(0,8),CoordinateImpl(0,8)),
         Tuple2(CoordinateImpl(14,8),CoordinateImpl(14,8)),
         Tuple2(CoordinateImpl(11,6),CoordinateImpl(12,7)))

  override val entryCoordinate: Coordinate = CoordinateImpl(7,8)

  setBasicTilesInMap()
  setNotWalkableArea()

  val boxCoordinate: Coordinate = CoordinateImpl(11, 1)
  map(boxCoordinate.x)(boxCoordinate.y) = Box()

  override def pokemonCharacter: List[PokemonCharacter] = null
}

/**
  * LaboratoryMap is the BuildingMap for the Laboratory
  */
class LaboratoryMap extends BuildingMap {
  override val height: Int = Settings.Constants.LABORATORY_INSIDE_HEIGHT
  override val width: Int = Settings.Constants.LABORATORY_INSIDE_WIDTH
  override val map: Array[Array[Tile]] = Array.ofDim[Tile](width, height)

  override var staticCharacter: StaticCharacter = new Oak

  override val image: Image = LoadImage.load(Settings.Images.MAP_IMAGES_FOLDER + "laboratory.png")

  override val matricesNotWalkable: List[Tuple2[Coordinate, Coordinate]] =
    List(Tuple2(CoordinateImpl(0,0),CoordinateImpl(12,1)),
         Tuple2(CoordinateImpl(0,3),CoordinateImpl(2,4)),
         Tuple2(CoordinateImpl(1,5),CoordinateImpl(2,5)),
         Tuple2(CoordinateImpl(8,4),CoordinateImpl(10,4)),
         Tuple2(CoordinateImpl(0,7),CoordinateImpl(4,8)),
         Tuple2(CoordinateImpl(8,7),CoordinateImpl(12,8)),
         Tuple2(CoordinateImpl(0,11),CoordinateImpl(0,12)),
         Tuple2(CoordinateImpl(12,11),CoordinateImpl(12,12)))

  override val entryCoordinate: Coordinate = CoordinateImpl(6,12)

  setBasicTilesInMap()
  setNotWalkableArea()

  private val bulbasaur: PokemonCharacter = PokemonCharacter(PokemonCharacter.InitialPokemon.Bulbasaur)
  private val charmander: PokemonCharacter = PokemonCharacter(PokemonCharacter.InitialPokemon.Charmander)
  private val squirtle: PokemonCharacter = PokemonCharacter(PokemonCharacter.InitialPokemon.Squirtle)
  override val pokemonCharacter: List[PokemonCharacter] = List(bulbasaur, charmander, squirtle)
}
