package view;

import controller.GameMenuController;
import model.entities.PokemonWithLife;

/**
 * PokemonInTeamPanel extends PokemonPanel adding the back button to go back in the menu.
 */
class PokemonInTeamPanel extends PokemonPanel{
    /**
     * @param pokemonWithLife the Pokemon you want to see specific info
     * @param gameMenuController instance of GameMenuController
     */
    PokemonInTeamPanel(PokemonWithLife pokemonWithLife, GameMenuController gameMenuController){
        setPokemon(pokemonWithLife);
        this.backButton.setVisible(true);
        this.backButton.addActionListener(e-> gameMenuController.showTeam());
        JUtil.setFocus(this);
        JUtil.setEscClick(this, this.backButton);
    }
}
