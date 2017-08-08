package model.map

import model.environment.{Coordinate, CoordinateImpl}
import model.map.CompositeElement.Lake
import model.map.Tile._
import org.scalatest.FunSuite

class MapElementsTest extends FunSuite {

  def fixture =
    new {
      val mapElements = MapElements()
      val coordinate1 = CoordinateImpl(1,1)
      val coordinate2 = CoordinateImpl(1,5)
      val coordinate3 = CoordinateImpl(3,3)
      val coordinates: Seq[Coordinate] = Seq(coordinate1, coordinate2, coordinate3)
      val tallGrass = TallGrass()
    }

  test("A new element is added to the map elements") {
    val f = fixture
    f.mapElements.addTile(f.tallGrass, f.coordinate1)
    assert(f.mapElements.map(f.coordinate1) == f.tallGrass)
  }

  test("An element is added in multiple coordinates to the map elements") {
    val f = fixture
    f.mapElements.addTileInMultipleCoordinates(f.tallGrass, f.coordinates)
    assert(f.mapElements.map(f.coordinate1) == f.tallGrass)
    assert(f.mapElements.map(f.coordinate2) == f.tallGrass)
    assert(f.mapElements.map(f.coordinate3) == f.tallGrass)
  }

  test("Multiple elements is added to the map elements") {
    val f = fixture
    f.mapElements.addMultipleElements(f.tallGrass, f.coordinate1, f.coordinate3)
    for (x <- f.coordinate1.x to f.coordinate3.x)
      for (y <- f.coordinate1.y to f.coordinate3.y)
        assert(f.mapElements.map(x,y) == f.tallGrass)

  }

  test("A lake is added to the map elements") {
    val f = fixture
    f.mapElements.addCompositeElement(Lake(), f.coordinate1, f.coordinate3)
    for (x <- f.coordinate1.x to f.coordinate3.x)
      for (y <- f.coordinate1.y to f.coordinate3.y)
        (x, y) match {
          case (f.coordinate1.x, f.coordinate1.y) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginTopLeft])
          case (f.coordinate1.x, f.coordinate3.y) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginBottomLeft])
          case (f.coordinate3.x, f.coordinate1.y) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginTopRight])
          case (f.coordinate3.x, f.coordinate3.y) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginBottomRight])
          case (f.coordinate1.x, _) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginLeft])
          case (f.coordinate3.x, _) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginRight])
          case (_, f.coordinate1.y) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginTop])
          case (_, f.coordinate3.y) => assert(f.mapElements.map(x, y).isInstanceOf[WaterMarginBottom])
          case _ => assert(f.mapElements.map(x, y).isInstanceOf[Water])
        }
  }
}
