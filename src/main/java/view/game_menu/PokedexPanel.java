package view.game_menu;

import controller.GameController;
import controller.GameMenuController;
import database.local.PokedexConnect;
import utilities.Settings;
import view.BasePanel;
import view.JUtil;
import view.LoadImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * This Panel paints a Pokedex with every Pokemon a trainer met with his id, name and image and a Pokeball if the
 * trainer has alreay captured it.
 */
public class PokedexPanel extends BasePanel {
    private static final int INFO_FONT_SIZE = Settings.Constants$.MODULE$.FRAME_SIDE()/32;
    private static final int FONT_SIZE = Settings.Constants$.MODULE$.FRAME_SIDE()/25;
    private static final int POKEMON_SIDE = (int) (Settings.Constants$.MODULE$.FRAME_SIDE()/5.8);
    private static final int POKEBALL_SIDE = Settings.Constants$.MODULE$.FRAME_SIDE()/22;
    private static final String TEXT1 = "      INFO:           CAUGHT: ";
    private static final String TEXT2 = "   SEEN: ";
    private static final String UNKNOWN_POKEMON = "???";

    /**
     * @param gameMenuController instance of GameMenuController
     * @param gameController instance of GameController
     */
    public PokedexPanel(GameMenuController gameMenuController, GameController gameController){
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.POKEDEX_PANEL_BACKGROUND());
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.setPreferredSize(new Dimension(Settings.Constants$.MODULE$.FRAME_SIDE(),Settings.Constants$.MODULE$.FRAME_SIDE()/6));
        this.add(northPanel, BorderLayout.NORTH);
        k.insets = new Insets(0,0,0,10);
        JLabel id, name, icon, captured, infoNumber;
        infoNumber = new JLabel(TEXT1 + gameController.trainer().capturedPokemonId().length() + TEXT2 + gameController.trainer().pokedex().pokedex().length());
        infoNumber.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, INFO_FONT_SIZE));
        infoNumber.setHorizontalAlignment(JLabel.CENTER);
        infoNumber.setForeground(Color.WHITE);
        northPanel.add(infoNumber,BorderLayout.CENTER);
        Image myImage;
        ImageIcon myImageIcon = null;
        for(int i = 1; i < 152; i++) {
            id = new JLabel(i + ")");
            if(gameController.trainer().pokedex().pokedex().contains(i)){
                name = new JLabel(PokedexConnect.getPokemonName(i).get().toUpperCase());
                try {
                    myImage = ImageIO.read(getClass().getResource(Settings.Images$.MODULE$.POKEMON_IMAGES_FRONT_FOLDER() + i + ".png"));
                    myImageIcon = new ImageIcon(myImage.getScaledInstance(POKEMON_SIDE,POKEMON_SIDE,java.awt.Image.SCALE_SMOOTH));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                icon = new JLabel(myImageIcon);
                if(gameController.trainer().capturedPokemonId().contains(i)){
                    try {
                        myImage = ImageIO.read(getClass().getResource(Settings.Images$.MODULE$.POKEBALL_ICON()));
                        myImageIcon = new ImageIcon(myImage.getScaledInstance(POKEBALL_SIDE,POKEBALL_SIDE,java.awt.Image.SCALE_SMOOTH));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    captured = new JLabel(myImageIcon);
                } else {
                    captured = new JLabel("");
                }
            } else {
                name = new JLabel(UNKNOWN_POKEMON);
                try {
                    myImage = ImageIO.read(getClass().getResource(Settings.Images$.MODULE$.UNKWOWN_POKEMON_IMAGE()));
                    myImageIcon = new ImageIcon(myImage.getScaledInstance(POKEMON_SIDE,POKEMON_SIDE,java.awt.Image.SCALE_SMOOTH));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                icon = new JLabel(myImageIcon);
                captured = new JLabel("");
            }
            id.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, FONT_SIZE));
            name.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, FONT_SIZE));
            this.centralPanel.add(id, k);
            this.centralPanel.add(name, k);
            this.centralPanel.add(icon, k);
            this.centralPanel.add(captured, k);
            k.gridy++;
        }

        this.backButton.addActionListener(e -> {
            gameController.resume();
            gameController.pause();
            gameMenuController.showGameMenu();
        });
        JScrollPane scroll = new JScrollPane(centralPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        this.add(scroll);
        JUtil.setFocus(this);
        JUtil.setEscClick(this, this.backButton);
    }

}
