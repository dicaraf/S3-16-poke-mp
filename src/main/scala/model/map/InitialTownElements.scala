package model.map

import model.environment.CoordinateImpl
import model.map.CompositeElement.{Lake, Square}
import utilities.Settings

/**
  * InitialTownElements represents the elements that compose the initial town map
  */
case class InitialTownElements() extends MapElementsImpl {
  import Tile._

  addBuildings()
  addLakes()
  addTallGrass()
  addTrees()

  addCompositeElement(Square(), (21,21), (28,28))
  addCompositeElement(Square(), (6,35), (16,45))
  addRoadFromSquareToPokemonCenter()
  addRoadFromSquareToLaboratory()
  addRoadFromSquareToBottomLeftLake()
  addRoadFromSquareToTopTallGrass()

  /**
    * adds the buildings to the map
    */
  private def addBuildings() ={
    addTile(PokemonCenter((10,19)), (10,19))
    addTile(Laboratory((40,20)), (40,20))
  }

  import Settings.Constants._

  /**
    * adds all trees to the map
    */
  private def addTrees() = {
    for (x <- 0 until MAP_WIDTH)
      for (y <- 0 until MAP_HEIGHT)
        if (x == 0 || x == MAP_WIDTH - 1 || y == 0 || y == MAP_HEIGHT - 1) addTile(Tree(), (x,y))

    addMultipleElements(Tree(), (1,18), (14,18))
    addMultipleElements(Tree(), (6,34), (8,34))
    addMultipleElements(Tree(), (6,46), (8,46))
    addMultipleElements(Tree(), (14,34), (16,34))
    addMultipleElements(Tree(), (14,46), (16,46))
    addMultipleElements(Tree(), (5,34), (5,37))
    addMultipleElements(Tree(), (17,34), (17,37))
    addMultipleElements(Tree(), (5,43), (5,46))
    addMultipleElements(Tree(), (17,43), (17,46))
    addMultipleElements(Tree(), (20,18), (20,23))
    addMultipleElements(Tree(), (9,28), (9,31))
    addMultipleElements(Tree(), (20,28), (20,31))
    addMultipleElements(Tree(), (10,31), (11,31))
    addMultipleElements(Tree(), (18,31), (19,31))
    addMultipleElements(Tree(), (15,18), (16,23))
    addMultipleElements(Tree(), (43,1), (49,4))
    addMultipleElements(Tree(), (35,32), (39,36))

    val trees = Seq[CoordinateImpl](CoordinateImpl(39,23), CoordinateImpl(47,23), CoordinateImpl(29, 23), CoordinateImpl(34,23))
    addTileInMultipleCoordinates(Tree(), trees)
  }

  /**
    * adds lakes to the map
    */
  private def addLakes() = {
    addCompositeElement(Lake(),(4,5), (20,10))
    addCompositeElement(Lake(),(40,40), (48,48))
    addCompositeElement(Lake(),(10,28), (19,30))
  }

  /**
    * adds the tall grass to the map
    */
  private def addTallGrass() = {
    addMultipleElements(TallGrass(), (1,1), (42,4))
    addMultipleElements(TallGrass(), (1,5), (3,10))
    addMultipleElements(TallGrass(), (1,19), (9,23))
    addMultipleElements(TallGrass(), (1,24), (8,27))
    addMultipleElements(TallGrass(), (1,28), (8,31))
    addMultipleElements(TallGrass(), (37,5), (49,11))
    addMultipleElements(TallGrass(), (17,18), (19,23))
    addMultipleElements(TallGrass(), (38,1), (38,8))
    addMultipleElements(TallGrass(), (36,1), (37,5))
    addMultipleElements(TallGrass(), (42,11), (48,13))
    addMultipleElements(TallGrass(), (23,47), (39,48))
    addMultipleElements(TallGrass(), (33,31), (41,37))
  }

  /**
    * adds a road form the center square to the pokemon center
    */
  private def addRoadFromSquareToPokemonCenter() = {
    for (x <- 9 to 21)  x match {
      case 9 =>
        addTile(RoadMarginTopLeft(),(x,24))
        addTile(RoadMarginLeft(), (x,25))
        addTile(RoadMarginLeft(), (x,26))
        addTile(RoadMarginBottomLeft(),(x,27))
      case 21 =>
        addTile(Road(), (x,24))
        addTile(Road(), (x,25))
        addTile(Road(), (x,26))
        addTile(Road(), (x,27))
      case _ =>
        addTile(RoadMarginTop(), (x,24))
        addTile(Road(), (x,25))
        addTile(Road(), (x,26))
        addTile(RoadMarginBottom(), (x,27))
    }
  }

  /**
    * adds a road form the central square to the laboratory
    */
  private def addRoadFromSquareToLaboratory() = {
    for (x <- 28 to 47)  x match {
      case 28 =>
        addTile(Road(), (x,24))
        addTile(Road(), (x,25))
        addTile(Road(), (x,26))
        addTile(Road(), (x,27))
      case 47 =>
        addTile(RoadMarginTopRight(),(x,24))
        addTile(RoadMarginRight(), (x,25))
        addTile(RoadMarginRight(), (x,26))
        addTile(RoadMarginBottomRight(),(x,27))
      case _ =>
        addTile(RoadMarginTop(), (x,24))
        addTile(Road(), (x,25))
        addTile(Road(), (x,26))
        addTile(RoadMarginBottom(), (x,27))
    }
  }

  /**
    * adds a road from the center square to the bottom left lake
    */
  private def addRoadFromSquareToBottomLeftLake() = {
    for (y <- 28 to 46)  y match {
      case 28 =>
        addTile(Road(), (23,y))
        addTile(Road(), (24,y))
        addTile(Road(), (25,y))
        addTile(Road(), (26,y))
      case 46 =>
        addTile(RoadMarginBottomLeft(),(23,y))
        addTile(RoadMarginBottom(), (24,y))
        addTile(RoadMarginBottom(), (25,y))
        addTile(RoadMarginBottomRight(),(26,y))
      case _ =>
        addTile(RoadMarginLeft(), (23,y))
        addTile(Road(), (24,y))
        addTile(Road(), (25,y))
        addTile(RoadMarginRight(), (26,y))
    }

    for (x <- 26 to 39) x match {
      case 26 =>
        addTile(Road(), (x,43))
        addTile(Road(), (x,44))
        addTile(Road(), (x,45))
        addTile(RoadMarginBottom(), (x,46))
      case 39 =>
        addTile(RoadMarginTopRight(),(x,43))
        addTile(RoadMarginRight(), (x,44))
        addTile(RoadMarginRight(), (x,45))
        addTile(RoadMarginBottomRight(),(x,46))
      case _ =>
        addTile(RoadMarginTop(), (x,43))
        addTile(Road(), (x,44))
        addTile(Road(), (x,45))
        addTile(RoadMarginBottom(), (x,46))
    }
  }

  /**
    * adds a road from the central square to the top tall grass
    */
  private def addRoadFromSquareToTopTallGrass() = {
    for (y <- 6 to 21)  y match {
      case 6 =>
        addTile(RoadMarginTopLeft(),(23,y))
        addTile(RoadMarginTop(), (24,y))
        addTile(RoadMarginTop(), (25,y))
        addTile(RoadMarginTopRight(),(26,y))
      case 21 =>
        addTile(Road(), (23,y))
        addTile(Road(), (24,y))
        addTile(Road(), (25,y))
        addTile(Road(), (26,y))
      case _ =>
        addTile(RoadMarginLeft(), (23,y))
        addTile(Road(), (24,y))
        addTile(Road(), (25,y))
        addTile(RoadMarginRight(), (26,y))
    }

    for (x <- 26 to 36) x match {
      case 26 =>
        addTile(RoadMarginTop(), (x,6))
        addTile(Road(), (x,7))
        addTile(Road(), (x,8))
        addTile(Road(), (x,9))
      case 36 =>
        addTile(RoadMarginTopRight(), (x,6))
        addTile(RoadMarginRight(), (x,7))
        addTile(RoadMarginRight(), (x,8))
        addTile(RoadMarginBottomRight(), (x,9))
      case _ =>
        addTile(RoadMarginTop(), (x,6))
        addTile(Road(), (x,7))
        addTile(Road(), (x,8))
        addTile(RoadMarginBottom(), (x,9))
    }
  }

}