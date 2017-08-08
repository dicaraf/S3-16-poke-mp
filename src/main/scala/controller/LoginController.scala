package controller

import java.util.Optional
import javax.swing.JOptionPane

import com.rabbitmq.client.Connection
import database.remote.DBConnect
import distributed.client.PlayerLoginClientManager
import distributed.{ConnectedPlayers, DistributedConnection, PlayerImpl}
import model.entities.{Trainer, TrainerSprites}
import utilities.Settings
import view.View

/**
  * LoginController manages possible actions that the user can do in the LoginPanel
  */
trait LoginController{
  /**
    * Allows the user to access the game
    * @param username user username
    * @param password user password
    */
  def login(username: String, password: String): Unit

  /**
    * Returns to the initial game menu
    */
  def back(): Unit
}

object LoginController{
    def apply(initialMenuController: InitialMenuController, view: View): LoginController = new LoginControllerImpl(initialMenuController, view)
}

object LoginControllerImpl {
  private final val LOGIN_FAILED: String = "LOGIN FAILED"
}

/**
  * @inheritdoc
  * @param initialMenuController instance of the initial menu controller
  * @param view instance of the view
  */
class LoginControllerImpl(private val initialMenuController: InitialMenuController,
                          private val view: View) extends LoginController{

  import LoginControllerImpl._
  view showLogin this

  /**
    * @inheritdoc
    * @param username user username
    * @param password user password
    */
  override def login(username: String, password: String): Unit = {
    view.getLoginPanel.changeLoginButton("Connecting...")
    new Thread(() => {
      if(username == "" || password == "") {
        view.showMessage(Settings.Strings.LOGIN_ERROR_USERNAME_PASSWORD_EMPTY, LOGIN_FAILED, JOptionPane.ERROR_MESSAGE)
        view.getLoginPanel.changeLoginButton("Submit")
      }else{
        if (!DBConnect.checkCredentials(username, password)) {
          view.showMessage(Settings.Strings.LOGIN_ERROR_WRONG_USERNAME_PASSWORD, LOGIN_FAILED, JOptionPane.ERROR_MESSAGE)
          view.getLoginPanel.changeLoginButton("Submit")
        } else {
          newGame(username)
        }
      }
    }).start()
  }

  /**
    * Starts a new game
    * @param username user username
    */
  private def newGame(username: String) = {
    val optionalTrainer: Optional[Trainer] = DBConnect getTrainerFromDB username
    if(optionalTrainer.isPresent) {
      val trainer = optionalTrainer.get()
      DBConnect.setMyTrainer(trainer)
      val connection = DistributedConnection().connection
      val connectedPlayers = ConnectedPlayers()
      val mapController = MapController(view, trainer, connection, connectedPlayers)

      serverInteraction(connection, username, trainer, connectedPlayers)
      mapController.start()
      initialMenuController.stopMainMusic()

    } else {
      view.showMessage(Settings.Strings.LOGIN_NO_TRAINER_ERROR, LOGIN_FAILED, JOptionPane.ERROR_MESSAGE)
    }
  }

  /**
    * Interacts with the server to register the user and receive the players present within the game
    * @param connection instance of connection with RabbitMQ
    * @param username user username
    * @param trainer user trainer
    * @param connectedPlayers players currently connected to the game
    */
  private def serverInteraction(connection: Connection, username: String, trainer: Trainer, connectedPlayers: ConnectedPlayers) = {
    val playerConnectionClientManager = PlayerLoginClientManager(connection)

    val player = PlayerImpl(trainer.id, username, TrainerSprites.getIdImageFromTrainerSprite(trainer.sprites))
    if (trainer.capturedPokemonId.isEmpty) player.isVisible = false

    playerConnectionClientManager sendPlayer player
    playerConnectionClientManager.receivePlayersConnected(trainer.id, connectedPlayers)
  }

  /**
    * @inheritdoc
    */
  override def back(): Unit = initialMenuController.show()
}
