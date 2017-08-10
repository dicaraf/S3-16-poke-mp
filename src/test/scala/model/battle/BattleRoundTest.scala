package model.battle

import java.util.Optional

import model.entities._
import org.scalatest.FunSuite

class BattleRoundTest extends FunSuite {

  def fixture =
    new {
      val myPokemon: PokemonWithLife = PokemonFactory.createPokemon(Owner.WILD,Optional.empty(),Optional.of(1)).get()
      val myBehaviour: PokemonBehaviour = new PokemonBehaviourImpl(myPokemon)
      val otherPokemon: PokemonWithLife = PokemonFactory.createPokemon(Owner.WILD,Optional.empty(),Optional.of(1)).get()
      val otherBehaviour: PokemonBehaviour = new PokemonBehaviourImpl(otherPokemon)
    }

  test("Check pokemon attacks during battle") {
    val f = fixture
    val myPokemonInitialLife = f.myPokemon.pokemonLife
    val otherPokemonInitialLife = f.otherPokemon.pokemonLife
    val battleRound: BattleRound = new BattleRoundImpl(f.myPokemon,0,f.otherPokemon,null)
    battleRound.myPokemonAttack(3)
    assert(f.otherPokemon.pokemonLife < otherPokemonInitialLife)
    battleRound.otherPokemonAttack(3)
    assert(f.myPokemon.pokemonLife < myPokemonInitialLife)
    info("When a pokemon attack, the other pokemon undergo attack correctly")
  }
}
