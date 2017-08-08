package view.map;

import controller.GameController;
import model.map.BuildingMap;
import utilities.Settings;

import java.awt.*;

/**
 * LaboratoryPanel shows the laboratory inside
 */
public class LaboratoryPanel extends BuildingPanel {

    private boolean emptyCaptures;
    private BuildingMap laboratoryMap;

    /**
     * @param gameController instance of GameController
     * @param laboratoryMap instance of BuildingMap
     * @param emptyCaptures true if the trainer has no pokemon captured
     */
    public LaboratoryPanel(final GameController gameController, final BuildingMap laboratoryMap, final boolean emptyCaptures) {
        super(gameController, laboratoryMap);
        this.laboratoryMap = laboratoryMap;
        this.emptyCaptures = emptyCaptures;
    }

    @Override
    protected synchronized void doPaint(final Graphics g){
        super.doPaint(g);
        if(emptyCaptures){
            for(int i = 0; i < laboratoryMap.pokemonCharacter().length(); i++){
                g.drawImage(laboratoryMap.pokemonCharacter().apply(i).image(),
                        super.centerX + laboratoryMap.pokemonCharacter().apply(i).coordinate().x()* Settings.Constants$.MODULE$.TILE_PIXEL(),
                        super.centerY + laboratoryMap.pokemonCharacter().apply(i).coordinate().y()*Settings.Constants$.MODULE$.TILE_PIXEL(), this);
            }
        }
    }
}
