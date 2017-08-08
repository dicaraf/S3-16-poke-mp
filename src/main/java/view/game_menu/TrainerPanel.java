package view.game_menu;

import controller.GameController;
import controller.GameMenuController;
import database.remote.DBConnect;
import utilities.Settings;
import view.BasePanel;
import view.JUtil;
import view.LoadImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * TrainerPanel shows main info about the playing trainer (avatar, name, level, experience points and ranking position).
 */
public class TrainerPanel extends BasePanel {

    /**
     * @param gameMenuController instance of GameMenuController
     * @param gameController instance of GameController
     */
    public TrainerPanel(GameMenuController gameMenuController, GameController gameController) {
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.TRAINER_PANEL_BACKGROUND());
        Image myImage;
        ImageIcon myImageIcon = null;
        try {
            myImage = ImageIO.read(getClass().getResource(gameController.trainer().sprites().frontS().image()));
            myImageIcon = new ImageIcon(myImage.getScaledInstance(32,32,java.awt.Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.centralPanel.add(new JLabel(myImageIcon));
        this.centralPanel.add(new JLabel(gameController.trainer().username()),k);
        k.gridy++;
        this.centralPanel.add(new JLabel("Level:"),k);
        this.centralPanel.add(new JLabel(""+gameController.trainer().level()),k);
        k.gridy++;
        this.centralPanel.add(new JLabel("Experience points: "),k);
        this.centralPanel.add(new JLabel(""+gameController.trainer().experiencePoints()),k);
        k.gridy++;
        this.centralPanel.add(new JLabel("Ranking position: "),k);
        this.centralPanel.add(new JLabel(String.valueOf(DBConnect.getTrainerRank(gameController.trainer().id()))),k);
        this.backButton.addActionListener(e -> {
            gameController.resume();
            gameController.pause();
            gameMenuController.showGameMenu();
        });
        JUtil.setFocus(this);
        JUtil.setEscClick(this, this.backButton);
    }
}
