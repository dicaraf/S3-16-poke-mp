package model.map

import model.environment.CoordinateImpl
import model.map.Tile._
import org.scalatest.FunSuite

class MapCreatorTest extends FunSuite {

  def fixture =
    new {
      val elements = MapElements()
      val tallGrass = TallGrass()
      val water = Water()
      val coordinateTallGrass = CoordinateImpl(0,0)
      val coordinateWater = CoordinateImpl(1,1)
      elements.addTile(tallGrass, coordinateTallGrass)
      elements.addTile(water, coordinateWater)
    }

  test("A new map is created with right elements") {
    val f = fixture
    val map = MapCreator.create(10, 10, f.elements)
    assert(map.map(0)(0) == f.tallGrass)
    assert(map.map(1)(1) == f.water)
  }

}
