package model.entities

import java.util.Optional

import database.local.PokedexConnect
import org.scalatest.FunSuite

class PokemonDBTest extends FunSuite {

  test("Check pokemon name") {
    assert(PokedexConnect.getPokemonName(1).get() == "bulbasaur")
    info("getPokemonName works correctly")
  }

  test("Check pokemon attacks") {
    assert(PokedexConnect.getPokemonAttack(1).get()._1 == "pound")
    assert(PokedexConnect.getPokemonAttack(1).get()._2 == 40)
    assert(PokedexConnect.getPokemonAttack(2).get()._1 == "karate-chop")
    assert(PokedexConnect.getPokemonAttack(2).get()._2 == 50)
    info("getPokemonAttack works correctly")
  }

  test("Check pokemon evolutions") {
    assert(PokedexConnect.pokemonHasToEvolve(1,15)._1 == false)
    assert(PokedexConnect.pokemonHasToEvolve(1,16)._1 == true)
    assert(PokedexConnect.pokemonHasToEvolve(1,16)._2.get() == 2)
    info("pokemonHasToEvolve works correctly")
  }

  test("Check the lists of possible wild pokemon") {
    assert(PokedexConnect.getPossibleWildPokemon(1).get().size == 10)
    assert(PokedexConnect.getPossibleWildPokemon(5).get().size == 12)
    assert(PokedexConnect.getPossibleWildPokemon(10).get().size == 41)
    assert(PokedexConnect.getPossibleWildPokemon(20).get().size == 33)
    assert(PokedexConnect.getPossibleWildPokemon(30).get().size == 30)
    assert(PokedexConnect.getPossibleWildPokemon(50).get().size == 10)
    assert(PokedexConnect.getPossibleWildPokemon(100).get().size == 6)
    info("getPossibleWildPokemon works correctly")
  }

  test("Check the level range of possible wild pokemon") {
    assert(PokedexConnect.getMinLevelWildPokemon(1) == 1)
    assert(PokedexConnect.getMaxLevelWildPokemon(1) == 16)
    assert(PokedexConnect.getMinLevelWildPokemon(2) == 16)
    assert(PokedexConnect.getMaxLevelWildPokemon(2) == 32)
    assert(PokedexConnect.getMinLevelWildPokemon(3) == 32)
    assert(PokedexConnect.getMaxLevelWildPokemon(3) == 70)
    info("getMinLevelWildPokemon and getMaxLevelWildPokemon works correctly")
  }

  test("Create new pokemon wild") {
    val p1: PokemonWithLife = PokemonFactory.createPokemon(Owner.WILD,Optional.empty(),Optional.of(5)).get()
  }

}
