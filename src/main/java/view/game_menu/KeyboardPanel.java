package view.game_menu;

import controller.GameController;
import controller.GameMenuController;
import utilities.Settings;
import view.BasePanel;
import view.JUtil;
import view.LoadImage;

import javax.swing.*;
import java.awt.*;

/**
 * KeyboardPanel shows the keyboard explanation for the user
 */
public class KeyboardPanel extends BasePanel {

    /**
     * @param gameMenuController instance of GameMenuController
     * @param gameController instance of GameController
     */
    public KeyboardPanel (GameMenuController gameMenuController, GameController gameController) {
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.KEYBOARD_PANEL_BACKGROUND());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        JLabel expl = new JLabel("Keyboard explanation");
        expl.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(),Font.BOLD,45));
        expl.setHorizontalAlignment(JLabel.CENTER);
        northPanel.add(expl,BorderLayout.CENTER);
        this.add(northPanel,BorderLayout.NORTH);

        this.centralPanel.add(createLabel("Space: interact with people/confirm choice during battle"), k);
        k.gridy++;
        this.centralPanel.add(createLabel("Up arrow: trainer moves up"), k);
        k.gridy++;
        this.centralPanel.add(createLabel("Right arrow: trainer moves right"), k);
        k.gridy++;
        this.centralPanel.add(createLabel("Down arrow: trainer moves down"), k);
        k.gridy++;
        this.centralPanel.add(createLabel("Left arrow: trainer moves left"), k);
        k.gridy++;
        this.centralPanel.add(createLabel("Esc: open game menu"), k);
        k.gridy++;

        this.backButton.addActionListener(e -> {
            gameController.resume();
            gameController.pause();
            gameMenuController.showGameMenu();
        });
        JUtil.setFocus(this);
        JUtil.setEscClick(this, this.backButton);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(),Font.PLAIN,18));
        return label;
    }
}
