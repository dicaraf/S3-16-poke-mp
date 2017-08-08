package app

import controller.InitialMenuController
import view.{View, ViewImpl}

object Main extends App {
  val view: View = new ViewImpl()
  val controller: InitialMenuController = InitialMenuController(view)
}
