package view.map;

import controller.DistributedMapController;
import controller.GameController;
import distributed.Player;
import distributed.PlayerPositionDetails;
import model.map.Building;
import model.map.GameMap;
import utilities.Settings;
import view.LoadImage;

import java.awt.*;
import java.util.concurrent.ConcurrentMap;

/**
 * MapPanel shows the main map of the game and all the elements that populate it (main trainer, buildings, other trainers)
 */
public class MapPanel extends GamePanelImpl {

    private GameMap gameMap;
    private GameController mapController;
    private DistributedMapController distributedMapController;
    private static final int OFFSET = 2;

    /**
     * @param mapController instance of GameController
     * @param distributedMapController instance of DistributedMapController
     * @param gameMap map that that will be drawn
     */
    public MapPanel(GameController mapController, DistributedMapController distributedMapController, GameMap gameMap) {
        super(mapController);
        this.gameMap = gameMap;
        this.mapController = mapController;
        this.distributedMapController = distributedMapController;
    }

    @Override
    protected void doPaint(Graphics g) {
        drawMapElements(g);
        drawBuildings(g);
        drawOtherTrainers(g);
        drawTrainer(g);
    }

    /**
     * Draws the visible area of the map
     * @param g instance of Graphics
     */
    private void drawMapElements(Graphics g){
        int initialX = calculateInitialCoordinate(this.getCurrentX());
        int finalX = calculateFinalCoordinate(this.getCurrentX());
        int initialY = calculateInitialCoordinate(this.getCurrentY());
        int finalY = calculateFinalCoordinate(this.getCurrentY());

        int initialMapX = (initialX <= 0 ) ? 0 : initialX;
        int finalMapX = (finalX >= Settings.Constants$.MODULE$.MAP_WIDTH() ) ? Settings.Constants$.MODULE$.MAP_WIDTH() : finalX;
        int initialMapY = (initialY <= 0 ) ? 0 : initialY;
        int finalMapY = (finalY >= Settings.Constants$.MODULE$.MAP_HEIGHT() ) ? Settings.Constants$.MODULE$.MAP_HEIGHT() : finalY;

        for (int x = initialMapX; x < finalMapX; x++) {
            for (int y = initialMapY; y < finalMapY; y++) {
                if (!(this.gameMap.map()[x][y] instanceof Building)) {
                    g.drawImage(LoadImage.load(this.gameMap.map()[x][y].image()),
                            this.calculateCoordinate(x, this.getCurrentX()),
                            this.calculateCoordinate(y, this.getCurrentY()),
                            null);
                }
            }
        }
    }

    /**
     * @param centerCoordinate center map coordinate x or y
     * @return the coordinate from which to start drawing
     */
    private int calculateInitialCoordinate(int centerCoordinate){
        return ((centerCoordinate - Settings.Constants$.MODULE$.FRAME_SIDE() / 2) / Settings.Constants$.MODULE$.TILE_PIXEL()) - OFFSET;
    }

    /**
     * @param centerCoordinate center map coordinate x or y
     * @return the coordinate to which to finish drawing
     */
    private int calculateFinalCoordinate(int centerCoordinate){
        return ((centerCoordinate + Settings.Constants$.MODULE$.FRAME_SIDE() / 2) / Settings.Constants$.MODULE$.TILE_PIXEL()) + OFFSET;
    }

    /**
     * Draws buildings on the map
     * @param g instance of Graphics
     */
    private void drawBuildings(Graphics g) {
        for (int x = 0; x < Settings.Constants$.MODULE$.MAP_WIDTH(); x++) {
            for (int y = 0; y < Settings.Constants$.MODULE$.MAP_HEIGHT(); y++) {
                if ((this.gameMap.map()[x][y] instanceof Building
                    && (((Building) this.gameMap.map()[x][y]).topLeftCoordinate().x() == x)
                    && (((Building) this.gameMap.map()[x][y])).topLeftCoordinate().y() == y)) {
                    g.drawImage(LoadImage.load(this.gameMap.map()[x][y].image()),
                            this.calculateCoordinate(x, this.getCurrentX()),
                            this.calculateCoordinate(y, this.getCurrentY()),
                            null);
                }
            }
        }
    }

    /**
     * Draws other players' trainers
     * @param g instance og Graphics
     */
    private void drawOtherTrainers(Graphics g){
        ConcurrentMap<Object, PlayerPositionDetails> map = this.distributedMapController.playersPositionDetails();
        if(!map.isEmpty()){
            for(Player player : this.distributedMapController.connectedPlayers().getAll().values()){
                if(player.isVisible()) {
                    PlayerPositionDetails positionDetails = map.get(player.userId());
                    g.drawImage(LoadImage.load((positionDetails.currentSprite().image())),
                            this.calculateCoordinate(positionDetails.coordinateX(), this.getCurrentX()),
                            this.calculateCoordinate(positionDetails.coordinateY(), this.getCurrentY()),
                            null);
                }
            }
        }
    }

    /**
     * @param coordinate coordinate x or y
     * @param centerCoordinate center coordinate x or y
     * @return the right coordinate to draw in pixels
     */
    private int calculateCoordinate(double coordinate, int centerCoordinate) {
        return this.coordinateInPixels(coordinate) - centerCoordinate + Settings.Constants$.MODULE$.FRAME_SIDE() / 2;
    }

    /**
     * @param currentCoordinate the current coordinate in tiles
     * @return the coordinate in pixels
     */
    private int coordinateInPixels(double currentCoordinate) {
        return (int)(currentCoordinate * Settings.Constants$.MODULE$.TILE_PIXEL());
    }

    /**
     * Draws the main trainer on the map
     * @param g instance of Graphics
     */
    private void drawTrainer(Graphics g){
        g.drawImage(LoadImage.load(this.mapController.trainer().currentSprite().image()),
                Settings.Constants$.MODULE$.FRAME_SIDE() / 2,
                Settings.Constants$.MODULE$.FRAME_SIDE() / 2,
                null);
    }

}
