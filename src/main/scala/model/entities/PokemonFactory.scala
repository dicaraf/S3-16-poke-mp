package model.entities

import java.util.Optional
import database.local.PokedexConnect
import database.remote.DBConnect
import scala.collection.mutable
import scala.util.Random


/**
  * PokemonFactory is a static class that returns an instance of a pokemon:
  *  - if the pokemon is wild, the pokemon will be created from zero (the level of the trainer allows to create pokemon even more rare and strong).
  *  - if the pokemon is owned by a trainer, the class get the values in input from the database online.
  */
object PokemonFactory {
  private final val LEVEL_STEP_ONE: Int = 8
  private final val LEVEL_STEP_TWO: Int = 12

  def createPokemon(owner: Owner.Value, databaseId: Optional[Int], trainerLevel: Optional[Int]): Optional[PokemonWithLife] = owner match {
    case Owner.TRAINER => {
      var optional: Optional[java.util.Map[_,_]] = DBConnect.getPokemonFromDB(databaseId.get())
      if(optional.isPresent) {
        var pokemonMap: java.util.Map[_,_] = optional.get()
        val pokemon: PokemonWithLife =
          new PokemonWithLifeImpl(Pokemon(id = pokemonMap.get("id").toString.toInt,
                                          name = pokemonMap.get("name").toString,
                                          attacks = pokemonMap.get("attacks").asInstanceOf[(Int,Int,Int,Int)],
                                          level = pokemonMap.get("level").toString.toInt,
                                          experiencePoints = pokemonMap.get("experiencePoints").toString.toInt,
                                          levelExperience = pokemonMap.get("levelExperience").toString.toInt,
                                          imageName = pokemonMap.get("idImage").toString),
                                  pokemonMap.get("lifePoints").toString.toInt)
        Optional.of(pokemon)
      } else {
        Optional.empty()
      }
    }
    case Owner.WILD => {
      val pokemonId: Int = generateCasualPokemonId(trainerLevel.get())
      val level: Int = generateLevelPokemon(trainerLevel.get(),pokemonId)
      val exp: Int = generateExperiencePokemon(pokemonId, level)
      val pokemon: PokemonWithLife =
        new PokemonWithLifeImpl(Pokemon(id = pokemonId,
                                        name = PokedexConnect.getPokemonName(pokemonId).get(),
                                        attacks = generateAttacksPokemon(pokemonId),
                                        level = level,
                                        experiencePoints = exp,
                                        levelExperience = Random.nextInt(exp),
                                        imageName = pokemonId+".png"),
                                exp)
      Optional.of(pokemon)
    }
    case Owner.INITIAL => {
      val pokemonId: Int = databaseId.get()
      val level: Int = 5
      val exp: Int = generateExperiencePokemon(pokemonId, level)
      val pokemon: PokemonWithLife =
        new PokemonWithLifeImpl(Pokemon(id = pokemonId,
          name = PokedexConnect.getPokemonName(pokemonId).get(),
          attacks = generateAttacksPokemon(pokemonId),
          level = level,
          experiencePoints = exp,
          levelExperience = Random.nextInt(exp),
          imageName = pokemonId+".png"),
          exp)
      Optional.of(pokemon)
    }
  }
  /**
    * Create the wild pokemon, considering the level of the trainer and the rarity of the pokemon.
    * When the coach level grows, you can find more Pokemon. Established the pokemon list that can be found,
    * a probability algorithm is created based on the rarity of pokemon.
    */
  private def generateCasualPokemonId(trainerLevel: Int): Int = {
    var indexRandomList: mutable.Map[Range,Int] = mutable.HashMap()
    if(trainerLevel < LEVEL_STEP_ONE) {
      indexRandomList = indexRandomList += ((0 until 50) -> 1) += ((50 until 80) -> 5) += ((80 until 100) -> 10)
    } else if (trainerLevel < LEVEL_STEP_TWO) {
      indexRandomList = indexRandomList += ((0 until 35) -> 1) += ((35 until 60) -> 5) += ((60 until 80) -> 10) +=
        ((80 until 90) -> 20) += ((90 until 96) -> 30) += ((96 until 100) -> 50)
    } else {
      indexRandomList = indexRandomList += ((0 until 35) -> 1) += ((35 until 60) -> 5) += ((60 until 80) -> 10) +=
        ((80 until 90) -> 20) += ((90 until 96) -> 30) += ((96 until 99) -> 50) += ((99 until 100) -> 100)
    }
    val random: Int = Random.nextInt(100)
    var pokemonId: Int = 0
    for ((k,v) <- indexRandomList) {
      if(k.contains(random)) {
        val pokemonList: java.util.List[Integer] = PokedexConnect.getPossibleWildPokemon(v).get()
        pokemonId = pokemonList.get(Random.nextInt(pokemonList.size()))
      }
    }
    pokemonId
  }
  /**
    * The level of the pokemon is created beetwen a possible range (contained in the database). Within this range,
    * the level of the pokemon will be generated considering the coach level.
    */
  private def generateLevelPokemon(trainerLevel: Int, pokemonId: Int): Int = {
    val min: Int = PokedexConnect.getMinLevelWildPokemon(pokemonId)
    val max: Int = PokedexConnect.getMaxLevelWildPokemon(pokemonId)
    val x: Int = (max - min)/3
    if(trainerLevel < LEVEL_STEP_ONE) {
      min + Random.nextInt(x)
    } else if(trainerLevel < LEVEL_STEP_TWO) {
      min + x + Random.nextInt(x)
    } else {
      min + 2*x + Random.nextInt(x)
    }
  }
  /**
    * A simple calculation to create the pokemon experience
    */
  private def generateExperiencePokemon(id: Int, level: Int): Int = {
    val baseExp: Int = PokedexConnect.getBaseExperiencePokemon(id).get()
    baseExp + level*4 - 4
  }
  /**
    * The attacks of the pokemon are created randomly, getting 4 attacks from the list of the possible attacks that the pokemon can learn
    */
  private def generateAttacksPokemon(id: Int): (Int,Int,Int,Int) = {
    var tempIndex: Int = 0
    val attacksList: java.util.List[Integer] = PokedexConnect.getAttacksList(id)
    var attacks: Seq[Int] = IndexedSeq()
    for(i <- 0 until 4) {
      tempIndex = Random.nextInt(attacksList.size())
      attacks = attacks :+ attacksList.get(tempIndex).toInt
      attacksList.remove(tempIndex)
    }
    (attacks.head,attacks(1),attacks(2),attacks(3))
  }

}
