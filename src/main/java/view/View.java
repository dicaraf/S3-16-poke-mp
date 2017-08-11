package view;

import controller.*;
import model.entities.PokemonWithLife;
import model.map.BuildingMap;
import model.map.GameMap;
import model.entities.Trainer;
import view.battle.BattleView;
import view.initial_menu.LoginPanel;
import view.map.GamePanelImpl;

import javax.swing.*;

/**
 * This interface allows to connect the view and the controller. In particular, View permits to change panel every time
 * that the controller asks for this.
 */
public interface View {
    void showInitialMenu(InitialMenuController initialMenuController);

    /**
     * Show the panel where the user can login
     * @param loginController
     */
    void showLogin(LoginController loginController);

    /**
     * Show the panel where the user can signin
     * @param signInController
     */
    void showSignIn(SignInController signInController);

    /**
     * Show the map background
     * @param mapController
     * @param distributedMapController
     * @param gameMap
     */
    void showMap(GameController mapController, DistributedMapController distributedMapController, GameMap gameMap);

    /**
     * Show the pokemon center background
     * @param pokemonCenterController
     * @param buildingMap
     */
    void showPokemonCenter(GameController pokemonCenterController, BuildingMap buildingMap);

    /**
     * Show the laboratory backoground
     * @param laboratoryController
     * @param buildingMap
     * @param emptyCaptures
     */
    void showLaboratory(GameController laboratoryController, BuildingMap buildingMap, boolean emptyCaptures);

    /**
     * Return the GamePanel
     * @return gamePanel
     */
    GamePanelImpl getGamePanel();

    /**
     * Show the computer box, at the pokemon center
     * @param buildingController
     */
    void showBoxPanel(BuildingController buildingController);

    /**
     * Show the initial pokemon choice, at the laboratory
     * @param buildingController
     * @param pokemon
     */
    void showInitialPokemonPanel(BuildingController buildingController, PokemonWithLife pokemon);

    /**
     * Show a dialog
     * @param dialoguePanel
     */
    void showDialogue(JPanel dialoguePanel);

    /**
     * Show the menu during the game
     * @param gameMenuController
     */
    void showGameMenuPanel(GameMenuController gameMenuController);

    /**
     * Show the battle background
     * @param myPokemon
     * @param otherPokemon
     * @param battleController
     */
    void showBattle(PokemonWithLife myPokemon, PokemonWithLife otherPokemon, BattleController battleController);

    /**
     * Return the BattlePanel
     * @return battlePanel
     */
    BattleView getBattlePanel();

    /**
     * Show the panel, which allow to change pokemon during the battle
     * @param battleController
     * @param trainer
     */
    void showPokemonChoice(BattleController battleController, Trainer trainer);

    /**
     * Show the pokedex (within the menu)
     * @param gameMenuController
     * @param gameController
     */
    void showPokedex(GameMenuController gameMenuController, GameController gameController);

    /**
     * Show the pokemon team status (the six pokemon in the bag, with related data)(within the menu)
     * @param gameMenuController
     * @param gameController
     */
    void showTeamPanel(GameMenuController gameMenuController, GameController gameController);

    /**
     * Show the specific characteristics of a pokemon (within the pokemon team)
     * @param pokemonWithLife
     * @param gameMenuController
     */
    void showPokemonInTeamPanel(PokemonWithLife pokemonWithLife, GameMenuController gameMenuController);

    /**
     * Show the trainer data and skills (within the menu)
     * @param gameMenuController
     * @param gameController
     */
    void showTrainerPanel(GameMenuController gameMenuController, GameController gameController);

    /**
     * Show the trainers' rank (within the menu)
     * @param gameMenuController
     * @param gameController
     */
    void showRankingPanel(GameMenuController gameMenuController, GameController gameController);

    /**
     * Show the explanation of the keyboard (within the menu)
     * @param gameMenuController
     * @param gameController
     */
    void showKeyboardPanel(GameMenuController gameMenuController, GameController gameController);

    /**
     * Show a message dialogue
     * @param error
     * @param title
     * @param messageType
     */
    void showMessage(final String error, final String title, final int messageType);

    /**
     * Return the LoginPanel
     * @return loginPanel
     */
    LoginPanel getLoginPanel();

}
