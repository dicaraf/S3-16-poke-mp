package model.map

import model.environment.CoordinateImpl
import model.map.Tile._
import org.scalatest.FunSuite

class GameMapTest extends FunSuite {

  def fixture =
    new {
      val gameMap = GameMap(10,10)
      val coordinate = CoordinateImpl(0,0)
      val tallGrass = TallGrass()
      val pokemonCenter = PokemonCenter(coordinate)
    }

  test("A game map should be filled with grass tiles") {
    val f = fixture
    f.gameMap.map foreach (element => element foreach ( element2 => assert(element2.isInstanceOf[Grass])))
  }

  test("A new single tile is added to the map") {
    val f = fixture
    f.gameMap.addTile(f.coordinate, f.tallGrass)
    assert(f.gameMap.map(f.coordinate.x)(f.coordinate.y) == f.tallGrass)
  }

  test("A new building is added to the map") {
    val f = fixture
    f.gameMap.addTile(f.coordinate, f.pokemonCenter)
    for (x <- f.coordinate.x until f.coordinate.x + f.pokemonCenter.width) {
      for (y <- f.coordinate.y until f.coordinate.y + f.pokemonCenter.height) {
        assert(f.gameMap.map(f.coordinate.x)(f.coordinate.y) == f.pokemonCenter)
      }
    }
    assert( f.gameMap.map(5)(5).isInstanceOf[Grass])
  }

}
