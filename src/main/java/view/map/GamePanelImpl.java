package view.map;

import controller.GameKeyListener;
import controller.GameController;
import utilities.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * GamePanelImpl abstract class provides template methods for subclasses that must paint a dynamic trainer on the map
 */
public abstract class GamePanelImpl extends JPanel implements GamePanel {

    private int currentX;
    private int currentY;

    /**
     * @param gameController instrance of GameController
     */
    public GamePanelImpl(GameController gameController) {
        this.setFocusable(true);
        this.requestFocusInWindow();
        KeyListener keyListener = GameKeyListener.apply(gameController);
        this.addKeyListener(keyListener);
        this.currentX = gameController.trainer().coordinate().x() * Settings.Constants$.MODULE$.TILE_PIXEL();
        this.currentY = gameController.trainer().coordinate().y() * Settings.Constants$.MODULE$.TILE_PIXEL();
    }

    @Override
    protected final synchronized void paintComponent(final Graphics g) {
        super.paintComponent(g);
        this.requestFocusInWindow();
        this.doPaint(g);
    }

    /**
     * @param g instance of Graphics
     */
    protected abstract void doPaint(final Graphics g);

    /**
     * @inheritdoc
     */
    public synchronized void updateCurrentX(double x) {
        this.currentX = (int)(x * Settings.Constants$.MODULE$.TILE_PIXEL());
    }

    /**
     * @inheritdoc
     */
    public synchronized void updateCurrentY(double y) {
        this.currentY = (int)(y * Settings.Constants$.MODULE$.TILE_PIXEL());
    }

    /**
     * @return current trainer's coordinate x
     */
    protected int getCurrentX() {
        return this.currentX;
    }

    /**
     * @return current trainer's coordinate y
     */
    protected int getCurrentY() {
        return this.currentY;
    }


}
