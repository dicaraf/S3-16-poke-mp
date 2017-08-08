package model.entities

import java.util.Optional

import org.scalatest.FunSuite

class PokemonBehaviourTest extends FunSuite {

  def fixture =
    new {
      val bulbasaur: PokemonWithLife = PokemonFactory.createPokemon(Owner.WILD,Optional.of(1),Optional.of(1)).get()
      val bulbasaurBehaviour: PokemonBehaviour = new PokemonBehaviourImpl(bulbasaur)
    }

  test("Check undergoing attack") {
    val f = fixture
    f.bulbasaurBehaviour.undergoAttack(100000)
    assert(!f.bulbasaurBehaviour.isAlive)
    assert(f.bulbasaur.pokemonLife == 0)
    info("Undergoing attack works correctly")
  }

}
