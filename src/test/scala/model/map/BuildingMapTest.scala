package model.map


import model.map.Tile._
import org.scalatest.FunSuite

class BuildingMapTest extends FunSuite {

  def fixture =
    new {
      val pokemonCenterMap: PokemonCenterMap = new PokemonCenterMap
      val laboratoryMap: LaboratoryMap = new LaboratoryMap
    }

  test("Test that every building map element is a Tile") {
    val f = fixture
    f.pokemonCenterMap.map foreach (element => element foreach (element2 => assert(element2.isInstanceOf[Tile])))
    f.laboratoryMap.map foreach (element => element foreach (element2 => assert(element2.isInstanceOf[Tile])))
  }

  test("Test box position") {
    val f = fixture
    assert(f.pokemonCenterMap.map(f.pokemonCenterMap.boxCoordinate.x)(f.pokemonCenterMap.boxCoordinate.y).isInstanceOf[Box])
  }

  test("Test Barrier position"){
    val f = fixture
    testBarrier(f.laboratoryMap)
  }

  def testBarrier(buildingMap: BuildingMap): Unit = {
    for(matrixNotWalkable <- buildingMap.matricesNotWalkable){
      for( i <- matrixNotWalkable._1.x to matrixNotWalkable._2.x){
        for( j <- matrixNotWalkable._1.y to matrixNotWalkable._2.y){
          assert(buildingMap.map(i)(j).isInstanceOf[Barrier])
        }
      }
    }
  }

}
