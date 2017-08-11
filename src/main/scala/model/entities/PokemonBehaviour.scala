package model.entities

import database.local.PokedexConnect
import database.remote.DBConnect

import scala.collection.immutable.HashMap

/**
  * PokemonBehaviour rapresent the behaviour of a pokemon during and after the battle. Every time that a pokemon is created,
  * in the code will be created a PokemonBehaviour that take in input the instance of the pokemon.
  */

trait PokemonBehaviour {

  /**
    * This method is called when a pokemon attacks the others pokemon. Take in input the id of the attack.
    * @param attackId
    * Returns the damage of the attack, that is calculated considering the attack and the pokemon's level.
    * @return the damage
    */
  def launchAttack(attackId: Int): Int

  /**
    * This method is called when a pokemon ungergo an attack. Check if the pokemon is death and eventually sets the flag.
    * @param damage
    */
  def undergoAttack(damage: Int): Unit

  /**
    * Return true if the pokemon is alive, otherwise false.
    * @return pokemon life status
    */
  def isAlive: Boolean

  /**
    * When the round beetween 2 pokemon is finished, the pokemon death give some experience pointe to other pokemon.
    * @return experience points
    */
  def giveExperiencePoints: Int

  /**
    * When the round beetween 2 pokemon is finished, the pokemon alive receive some experience pointe from other pokemon.
    * @param points
    */
  def growExperiencePoints(points: Int): Unit

  /**
    * Every time a round is finished, the values of a trainer's pokemon in database ​​are updated.
    * @param databaseId
    */
  def updatePokemonTrainer(databaseId: Int): Unit

  /**
    * When a wild pokemon is catched from a trainer, the pokemon (and his values) are inserted in database.
    * @param trainerId
    */
  def insertPokemonIntoDB(trainerId: Int): Unit
}

class PokemonBehaviourImpl(var pokemonWithLife: PokemonWithLife) extends PokemonBehaviour {
  private val ATTACK_PERCENTAGE: Double = 0.2
  private val GIVE_EXPERIENCE_PERCENTAGE: Double = 0.40
  private val UNDERGO_PERCENTAGE: Double = 1.01
  private var _isAlive: Boolean = true
  private var pokemonExperienceGrown = 0

  /**
    * @inheritdoc
    */
  override def launchAttack(attackId: Int): Int = {
    val attack: (String,Integer) = PokedexConnect.getPokemonAttack(attackId).get()
    (attack._2 * math.pow(pokemonWithLife.pokemon.level,0.3)).toInt
  }
  /**
    * @inheritdoc
    */
  override def undergoAttack(damage: Int): Unit = {
    val damageWeighed: Int = (damage/math.pow(UNDERGO_PERCENTAGE,pokemonWithLife.pokemon.level)).toInt
    val pokemonStatus: PokemonStatus.Value = pokemonWithLife.loseLifePoints(damageWeighed)
    if(pokemonStatus == PokemonStatus.DEATH) { _isAlive = false }
  }
  /**
    * @inheritdoc
    */
  override def isAlive = this._isAlive
  /**
    * @inheritdoc
    */
  override def giveExperiencePoints: Int = {
    (pokemonWithLife.pokemon.experiencePoints * GIVE_EXPERIENCE_PERCENTAGE).toInt
  }
  /**
    * @inheritdoc
    */
  override def growExperiencePoints(points: Int): Unit = {
    this.pokemonExperienceGrown = points
  }
  /**
    * Check, when the experience grows, if the pokemon level grows (and eventually has to evolve).
    */
  private def checkIfLevelGrows: (Int,Int,Int,Int) = {
    if(this.pokemonExperienceGrown > 0) {
      var newExpPoints = this.pokemonWithLife.pokemon.levelExperience + this.pokemonExperienceGrown
      val levelExp = this.pokemonWithLife.pokemon.experiencePoints + 4
      if (newExpPoints >= levelExp) {
        (checkEvolution(this.pokemonWithLife.pokemon.level+1),
          this.pokemonWithLife.pokemon.level+1,
          this.pokemonWithLife.pokemon.experiencePoints+4,
          newExpPoints - levelExp + 4)
      } else {
        (this.pokemonWithLife.pokemon.id,this.pokemonWithLife.pokemon.level,this.pokemonWithLife.pokemon.experiencePoints,newExpPoints)
      }
    } else {
      (this.pokemonWithLife.pokemon.id, this.pokemonWithLife.pokemon.level, this.pokemonWithLife.pokemon.experiencePoints,
        this.pokemonWithLife.pokemon.levelExperience)
    }
  }
  /**
    * Check, when the experience grows, if the pokemon has to evolve.
    */
  private def checkEvolution(newLevel: Int): Int = {
    val result = PokedexConnect.pokemonHasToEvolve(this.pokemonWithLife.pokemon.id ,newLevel)
    if(result._1) {
      DBConnect.addMetPokemon(DBConnect.getMyTrainer.id,result._2.get().toInt)
      DBConnect.addCapturedPokemon(DBConnect.getMyTrainer.id,result._2.get().toInt)
      result._2.get()
    } else {
      this.pokemonWithLife.pokemon.id
    }
  }
  /**
    * @inheritdoc
    */
  override def updatePokemonTrainer(databaseId: Int): Unit = {
    val pokemonData = checkIfLevelGrows
    val pokemon = HashMap(
      "id" -> pokemonData._1.toString,
      "level" -> pokemonData._2.toString,
      "points" -> pokemonData._3.toString,
      "level_exp" -> pokemonData._4.toString,
      "life" -> this.pokemonWithLife.pokemonLife.toString
    )
    DBConnect.updatePokemon(pokemon,databaseId)
  }
  /**
    * @inheritdoc
    */
  override def insertPokemonIntoDB(trainerId: Int): Unit = {
    DBConnect.insertWildPokemonIntoDB(pokemonWithLife: PokemonWithLife, trainerId: Int)
    DBConnect.addCapturedPokemon(trainerId: Int, pokemonWithLife.pokemon.id: Int)
  }
}
