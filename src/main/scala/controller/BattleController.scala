package controller

import model.battle.{Battle, BattleImpl}
import model.entities.Owner
import model.environment.{Audio, AudioImpl}
import utilities.Settings
import view.View

import scala.util.Random

/**
  * BattleController permits to manage every event during a battle against a wild Pokemon (attacks during the fight,
  * launching Pokeball and capturing wild Pokemon, allow trainer to change his Pokemons...)
  */
trait BattleController {
  /**
    * Manage the attack of the trainer's Pokemon
    * @param attackId the id of the attack that the trainer chose
    */
  def myPokemonAttacks(attackId: Int): Unit

  /**
    * Return how many Pokeballs are still available
    * @return available Pokeball number
    */
  def getPokeballAvailableNumber: Int

  /**
    * Manage whats happens when the trainer launch a Pokeball and return false if the trainers fails, true if the
    * trainer captures the Pokemon
    * @return true if the wild pokemon was captured, false in the opposite case
    */
  def trainerThrowPokeball(): Boolean

  /**
    * Show the panel that allow to change trainer's Pokemon in the battle
    */
  def changePokemon(): Unit

  /**
    * Update trainer's pokemon in the remote DB and create a new BattleRound with the new trainer's Pokemon selected.
    * In the end make wild Pokemon attack
    * @param id id of the new trainer's Pokemon
    */
  def pokemonToChangeIsSelected(id: Int): Unit

  /**
    * Return randomly if the trainer can escape from the battle or not; if not, make wild pokemon attack
    * @return true if the trainer can escape, false in the opposite case
    */
  def trainerCanQuit(): Boolean

  /**
    * Stop the battle audio and resume the game
    */
  def resumeGame(): Unit

  /**
    * Return if the current battle is with a wild Pokemon or with an other trainer
    * @return a boolean representing if the battle is distributed or not
    */
  def isDistributedBattle: Boolean = false

  /**
    * In a battle against a wild Pokemon return always false; in a distributed battle return if the playing trainer is
    * the first to attack or not
    * @return a boolean representing if the playing trainer is the first to attack
    */
  def yourPlayerIsFirst: Boolean = false
}

/**
  * @inheritdoc
  * @param controller Instance of game controller
  * @param view Instance of the view to update battle panel during the fight
  */
class BattleControllerImpl(private val controller: GameController, private val view: View) extends BattleController {
  private val WILD_POKEMON: Int = 0
  private val MY_POKEMON: Int = 1
  val battle: Battle = new BattleImpl(controller.trainer,this)
  private var timer: Thread = _
  battle.startBattleRound(controller.trainer.getFirstAvailableFavouritePokemon)
  showNewView()
  private val audio: Audio = Audio(Settings.Audio.POKEMON_WILD_SONG)
  audio.loop()

  private val audioCaptureFailed: Audio = Audio(Settings.Audio.CAPTURE_FAILED_SONG)
  private val audioCapture: Audio = Audio(Settings.Audio.CAPTURE_SONG)

  /**
    * Manage the attack of trainer's Pokemon and, if the wild Pokemon is still alive, make the wild Pokemon attacks.
    * Update battle view as well
    * @param attackId the id of the attack that the trainer chose
    */
  override def myPokemonAttacks(attackId: Int): Unit = {
    battle.round.myPokemonAttack(attackId)
    view.getBattlePanel.setPokemonLifeProgressBar(battle.otherPokemon.pokemonLife,Owner.WILD.id)
    if(!battle.battleFinished) {
      timer = new Thread() {
        override def run() {
          Thread.sleep(3000)
          otherPokemonAttacks()
        }
      }
      timer.start()
    } else {
      pokemonIsDead(WILD_POKEMON)
    }
  }

  /**
    * Chose randomly one of the possible wild Pokemon's attacks and attack trainer's Pokemon updating battle view and
    * checking if trainer's Pokemon died
    */
  private def otherPokemonAttacks(): Unit = {
    battle.round.otherPokemonAttack(view.getBattlePanel.getOtherPokemonAttacks()(Random.nextInt(3)))
    view.getBattlePanel.setPokemonLife()
    view.getBattlePanel.setPokemonLifeProgressBar(battle.myPokemon.pokemonLife,Owner.TRAINER.id)
    if(battle.roundFinished) {
      pokemonIsDead(MY_POKEMON)
    }
  }

  /**
    * @inheritdoc
    */
  override def changePokemon(): Unit = {
    view.showPokemonChoice(this, controller.trainer)
  }

  /**
    * @inheritdoc
    * @param id id of the new trainer's Pokemon
    */
  override def pokemonToChangeIsSelected(id: Int): Unit =  {
    battle.updatePokemonAndTrainer(Settings.Constants.BATTLE_EVENT_CHANGE_POKEMON)
    battle.startBattleRound(id)
    showNewView()
    pokemonWildAttacksAfterTrainerChoice()
  }

  /**
    * @inheritdoc
    * @return available Pokeball number
    */
  override def getPokeballAvailableNumber: Int = {
    battle.pokeball
  }

  /**
    * @inheritdoc
    * @return true if the wild pokemon was captured, false in the opposite case
    */
  override def trainerThrowPokeball(): Boolean = {
    battle.pokeball_=(battle.pokeball-1)
    if(!battle.pokemonIsCaptured()) {
      audioCaptureFailed.play()
      pokemonWildAttacksAfterTrainerChoice()
      false
    } else {
      audioCapture.play()
      timer = new Thread() {
        override def run() {
          battle.updatePokemonAndTrainer(Settings.Constants.BATTLE_EVENT_CAPTURE_POKEMON)
          Thread.sleep(3000)
          resumeGame()
        }
      }
      timer.start()
      true
    }
  }

  /**
    * @inheritdoc
    * @return true if the trainer can escape, false in the opposite case
    */
  override def trainerCanQuit(): Boolean = {
    if (Random.nextDouble()<0.5) {
      battle.updatePokemonAndTrainer(Settings.Constants.BATTLE_EVENT_ESCAPE)
      resumeGame()
      true
    } else {
      pokemonWildAttacksAfterTrainerChoice()
      false
    }
  }

  /**
    * @inheritdoc
    */
  override def resumeGame(): Unit = {
    audio.stop()
    audioCapture.stop()
    audioCaptureFailed.stop()
    controller.resume()
  }
  /**
    * Show a new view when the round changes
    */
  private def showNewView(): Unit = {
    view.showBattle(battle.myPokemon,battle.otherPokemon,this)
  }
  /**
    * When a pokemon dead, manage the situation
    */
  private def pokemonIsDead(pokemonDeadId: Int): Unit = {
    timer = new Thread() {
      override def run() {
        view.getBattlePanel.pokemonIsDead(pokemonDeadId)
        Thread.sleep(2000)
        if(pokemonDeadId==MY_POKEMON && !battle.battleFinished) {
          showNewView()
        } else if (pokemonDeadId==WILD_POKEMON) {
          resumeGame()
        }

      }
    }
    timer.start()
  }
  /**
    * When trainer changes pokemon or launch a pokeball, can't attack too. So this method allows to wild pokemon to attacks.
    */
  private def pokemonWildAttacksAfterTrainerChoice(): Unit = {
    timer = new Thread() {
      override def run() {
      view.getBattlePanel.pokemonWildAttacksAfterTrainerChoice()
      Thread.sleep(2000)
      otherPokemonAttacks()
      }
    }
    timer.start()
  }
}