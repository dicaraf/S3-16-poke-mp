package view.map;

import controller.GameController;
import model.map.BuildingMap;
import utilities.Settings;
import view.LoadImage;

import java.awt.*;

/**
 * BuildingPanel shows the building inside and all the elements and static characters that populate it
 */
public class BuildingPanel extends GamePanelImpl {
    protected int centerX;
    protected int centerY;
    private BuildingMap buildingMap;
    private GameController gameController;

    /**
     * @param gameController instance of GameController
     * @param buildingMap instance of BuildingMap
     */
    BuildingPanel(final GameController gameController, final BuildingMap buildingMap) {
        super(gameController);

        this.buildingMap = buildingMap;
        this.gameController = gameController;

        centerX = (Settings.Constants$.MODULE$.SCREEN_WIDTH()/3 - buildingMap.image().getWidth(null))/2;
        centerY = (Settings.Constants$.MODULE$.SCREEN_WIDTH()/3 - buildingMap.image().getHeight(null))/2;

    }

    @Override
    protected void doPaint(final Graphics g) {
        g.drawImage(this.buildingMap.image(), centerX, centerY, this);

        g.drawImage(LoadImage.load(this.gameController.trainer().currentSprite().image()),
                centerX+super.getCurrentX(), centerY+super.getCurrentY(), this);

        g.drawImage(buildingMap.staticCharacter().image(), centerX+(buildingMap.staticCharacter().coordinate().x())* Settings.Constants$.MODULE$.TILE_PIXEL(),
                centerY+(buildingMap.staticCharacter().coordinate().y())*Settings.Constants$.MODULE$.TILE_PIXEL() -
                        (buildingMap.staticCharacter().HEIGHT()-Settings.Constants$.MODULE$.TILE_PIXEL()), this);
    }

}
