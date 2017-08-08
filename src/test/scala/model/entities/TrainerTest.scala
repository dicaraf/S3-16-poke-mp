package model.entities

import java.util.Optional
import javax.swing.JTextField

import database.remote.DBConnect
import org.scalatest.FunSuite

import scala.collection.mutable

class TrainerTest extends FunSuite{

  def fixture =
    new {
      val map: mutable.Map[String, JTextField] = scala.collection.mutable.Map[String,JTextField]()
      val name = new JTextField("prova")
      val surname = new JTextField("prova")
      val email = new JTextField("prova@prova.it")
      val username = new JTextField("test")
      val password = new JTextField("testtest")
      map += "Name" -> name
      map += "Surname" -> surname
      map += "Email" -> email
      map += "Username" -> username
      map += "Password" -> password
      DBConnect.insertCredentials(collection.JavaConverters.mapAsJavaMap(map),3)
      val pokemon: PokemonWithLife = PokemonFactory.createPokemon(Owner.WILD,Optional.empty(),Optional.of(10)).get()
      val pokemonBehaviour: PokemonBehaviour = new PokemonBehaviourImpl(pokemon)
      val autoIncrementMet: Int = DBConnect.getAutoIncrement("pokemon_met")
      val autoIncrementCaptured: Int = DBConnect.getAutoIncrement("pokemon")
    }

  test("Create trainer from DB") {
    val f = fixture
    val trainer: Trainer = DBConnect.getTrainerFromDB("test").get()
    assert(trainer.username == "test")
    assert(trainer.experiencePoints == 0)
    assert(trainer.level == 1)
    DBConnect.deleteUserAndRelatedData("test")
  }

  test("New pokemon met, update pokedex") {
    val f = fixture
    val trainer: Trainer = DBConnect.getTrainerFromDB("test").get()
    trainer.addMetPokemon(23)
    assert(trainer.pokedex.pokedex == List(23))
    trainer.addMetPokemon(24)
    assert(trainer.pokedex.pokedex == List(23,24))
    trainer.addMetPokemon(23)
    assert(trainer.pokedex.pokedex == List(23,24))
    DBConnect.deleteUserAndRelatedData("test")
  }

  test("New pokemon added to favourite list") {
    val f = fixture
    val trainer: Trainer = DBConnect.getTrainerFromDB("test").get()
    f.pokemonBehaviour.insertPokemonIntoDB(trainer.id)
    f.pokemonBehaviour.insertPokemonIntoDB(trainer.id)
    trainer.capturedPokemons_=(DBConnect.getCapturedPokemonList(trainer.id).get())
    trainer.addFavouritePokemon(f.autoIncrementCaptured)
    assert(trainer.favouritePokemons == List(f.autoIncrementCaptured,0,0,0,0,0))
    trainer.addFavouritePokemon(f.autoIncrementCaptured+1)
    assert(trainer.favouritePokemons == List(f.autoIncrementCaptured,f.autoIncrementCaptured+1,0,0,0,0))
    trainer.addFavouritePokemon(f.autoIncrementCaptured)
    assert(trainer.favouritePokemons == List(f.autoIncrementCaptured,f.autoIncrementCaptured+1,0,0,0,0))
    DBConnect.deleteUserAndRelatedData("test")
  }

  test("Change pokemon in favourite list") {
    val f = fixture
    val trainer: Trainer = DBConnect.getTrainerFromDB("test").get()
    f.pokemonBehaviour.insertPokemonIntoDB(trainer.id)
    f.pokemonBehaviour.insertPokemonIntoDB(trainer.id)
    trainer.capturedPokemons_=(DBConnect.getCapturedPokemonList(trainer.id).get())
    trainer.addFavouritePokemon(f.autoIncrementCaptured)
    assert(trainer.favouritePokemons == List(f.autoIncrementCaptured,0,0,0,0,0))
    DBConnect.deleteUserAndRelatedData("test")
  }

  test("Update trainer") {
    val f = fixture
    val trainer: Trainer = DBConnect.getTrainerFromDB("test").get()
    assert(trainer.experiencePoints == 0)
    assert(trainer.level == 1)
    trainer.updateTrainer(200)
    assert(trainer.experiencePoints == 200)
    assert(trainer.level == 3)
    DBConnect.deleteUserAndRelatedData("test")
  }

  test("Get first pokemon with life in favourite pokemon list") {
    val f = fixture
    val trainer: Trainer = DBConnect.getTrainerFromDB("test").get()
    f.pokemonBehaviour.insertPokemonIntoDB(trainer.id)
    f.pokemonBehaviour.insertPokemonIntoDB(trainer.id)
    trainer.capturedPokemons_=(DBConnect.getCapturedPokemonList(trainer.id).get())
    trainer.addFavouritePokemon(f.autoIncrementCaptured)
    trainer.addFavouritePokemon(f.autoIncrementCaptured+1)
    assert(trainer.getFirstAvailableFavouritePokemon == f.autoIncrementCaptured)
    f.pokemonBehaviour.undergoAttack(100000)
    f.pokemonBehaviour.updatePokemonTrainer(f.autoIncrementCaptured)
    assert(trainer.getFirstAvailableFavouritePokemon == f.autoIncrementCaptured+1)
    DBConnect.deleteUserAndRelatedData("test")
  }

}
