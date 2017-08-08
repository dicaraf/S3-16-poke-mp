package view.game_menu;

import controller.GameMenuController;
import utilities.Settings;
import view.JUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * GameManuPanel shows the game menu which you can use to view different info panels with
 */
public class GameMenuPanel extends JPanel{

    /**
     * @param gameMenuController instance of GameMenuController
     */
    public GameMenuPanel(GameMenuController gameMenuController){
        setLayout(new GridLayout(0,1));
        final ButtonGroup buttonGroup = new ButtonGroup();
        final JRadioButton[] menuButtons = new JRadioButton[7];
        menuButtons[0] = new JRadioButton("Pokédex",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_POKEDEX_ICON()));
        menuButtons[1] = new JRadioButton("Team",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_TEAM_ICON()));
        menuButtons[2] = new JRadioButton("Trainer",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_TRAINER_ICON()));
        menuButtons[3] = new JRadioButton("Ranking",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_RANKING_ICON()));
        menuButtons[4] = new JRadioButton("Keyboard",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_KEYBOARD_ICON()));
        menuButtons[5] = new JRadioButton("Logout",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_LOGOUT_ICON()));
        menuButtons[6] = new JRadioButton("Exit",getImageIconByName(Settings.Images$.MODULE$.GAME_MENU_EXIT_ICON()));
        menuButtons[0].addActionListener(e -> gameMenuController.showPokedex());
        menuButtons[1].addActionListener(e -> gameMenuController.showTeam());
        menuButtons[2].addActionListener(e -> gameMenuController.showTrainer());
        menuButtons[3].addActionListener(e -> gameMenuController.showRanking());
        menuButtons[4].addActionListener(e -> gameMenuController.showKeyboardExplanation());
        menuButtons[5].addActionListener(e ->{
            int reply = JOptionPane.showConfirmDialog(null, Settings.Strings$.MODULE$.JOPTIONPANE_LOGOUT_MESSAGE(),
                    Settings.Strings$.MODULE$.JOPTIONPANE_LOGOUT_TITLE(), JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                gameMenuController.doLogout();
            }
            else {
                gameMenuController.doExit();
            }
        });
        menuButtons[6].addActionListener(e -> gameMenuController.doExit());
        for (JRadioButton menuButton : menuButtons) {
            menuButton.setBackground(Color.WHITE);
            buttonGroup.add(menuButton);
            add(menuButton);
        }
        menuButtons[0].setSelected(true);
        JUtil.setFocus(menuButtons[0]);
    }

    private ImageIcon getImageIconByName(String imageName){
        final Image myImage;
        ImageIcon myImageIcon = null;
        try {
            myImage = ImageIO.read(getClass().getResource(imageName));
            myImageIcon = new ImageIcon(myImage.getScaledInstance(32,32,java.awt.Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  myImageIcon;
    }
}
