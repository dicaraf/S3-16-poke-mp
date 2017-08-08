package view.map;

import controller.GameController;
import model.map.BuildingMap;

/**
 * PokemonCenterPanel shows the laboratory inside
 */
public class PokemonCenterPanel extends BuildingPanel {

    /**
     * @param gameController instance of GameController
     * @param pokemonCenterMap instance of BuildingMap
     */
    public PokemonCenterPanel(final GameController gameController, final BuildingMap pokemonCenterMap) {
        super(gameController, pokemonCenterMap);
    }
}
