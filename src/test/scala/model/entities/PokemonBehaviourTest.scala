package model.entities

import java.util.Optional

import org.scalatest.FunSuite

class PokemonBehaviourTest extends FunSuite {

  def fixture =
    new {
      val poke: PokemonWithLife = PokemonFactory.createPokemon(Owner.WILD,Optional.empty(),Optional.of(1)).get()
      val behaviour: PokemonBehaviour = new PokemonBehaviourImpl(poke)
    }

  test("Check launch attack") {
    val f = fixture
    var damage = f.behaviour.launchAttack(1)
    assert(damage == (40 * math.pow(f.poke.pokemon.level,0.3)).toInt)
    damage = f.behaviour.launchAttack(2)
    assert(damage == (50 * math.pow(f.poke.pokemon.level,0.3)).toInt)
    info("Launching attack works correctly")
  }

  test("Check undergoing attack") {
    val f = fixture
    f.behaviour.undergoAttack(100000)
    assert(!f.behaviour.isAlive)
    assert(f.poke.pokemonLife == 0)
    info("Undergoing attack works correctly")
  }

  test("Check pokemon life") {
    val f = fixture
    assert(f.behaviour.isAlive)
    f.behaviour.undergoAttack(100000)
    assert(!f.behaviour.isAlive)
    info("Life status works correctly")
  }

}
