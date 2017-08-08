package model.environment

/**
  * The actual direction of the trainer in the map
  */
object Direction extends Enumeration {
  type Direction = Value
  val UP, DOWN, LEFT, RIGHT = Value
}