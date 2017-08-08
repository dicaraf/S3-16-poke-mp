package view.map;

/**
 * GamePanel provides methods to paint a dynamic trainer on the map
 */
public interface GamePanel {
    /**
     * Updates the current trainer's coordinate x
     * @param x new coordinate x in double
     */
    void updateCurrentX(double x);

    /**
     * Updates the current trainer's coordinate y
     * @param y new coordinate y in double
     */
    void updateCurrentY(double y);
}
