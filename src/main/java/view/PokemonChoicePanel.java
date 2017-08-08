package view;

import controller.BattleController;
import model.entities.PokemonWithLife;
import model.entities.Trainer;
import scala.Tuple3;
import utilities.Settings;
import view.JUtil;
import view.game_menu.TeamPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * PokemonChoicPanel extends TeamPanel and it is used when you need to make the trainer choose a different Pokemon
 * (for example during a battle).
 */
class PokemonChoicePanel extends TeamPanel {
    private static final int FONT_SIZE = (int) (Settings.Constants$.MODULE$.FRAME_SIDE() * 0.034);
    private static final int infoSide = (int) (Settings.Constants$.MODULE$.FRAME_SIDE() * 0.05);

    /**
     * @param controller instance of BattleController
     * @param trainer instance of trainer
     */
    PokemonChoicePanel(BattleController controller, Trainer trainer) {
        super(null, null, trainer);
        this.backButton.setVisible(false);
        Image myImage;
        ImageIcon myImageIcon = null;
        try {
            myImage = ImageIO.read(getClass().getResource(Settings.Images$.MODULE$.INFO_BUTTON()));
            myImageIcon = new ImageIcon(myImage.getScaledInstance(infoSide,infoSide, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JButton info = new JButton(myImageIcon);
        info.setBorderPainted(false);
        info.setContentAreaFilled(false);
        info.setOpaque(false);
        this.downPanel.add(info,BorderLayout.WEST);
        Boolean first = true;
        for(Tuple3<JRadioButton,PokemonWithLife,Integer> pokemonRadioButton: buttonList){
            for(KeyListener kl: pokemonRadioButton._1().getKeyListeners()) pokemonRadioButton._1().removeKeyListener(kl);
            pokemonRadioButton._1().addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        for(Tuple3<JRadioButton,PokemonWithLife,Integer> radioButton: buttonList){
                            if(radioButton._1().isSelected()){
                                controller.pokemonToChangeIsSelected(radioButton._3());
                            }
                        }
                    }
                }
            });
            if(pokemonRadioButton._2().pokemonLife() == 0){
                pokemonRadioButton._1().setEnabled(false);
            }
            if(first && pokemonRadioButton._2().pokemonLife() != 0){
                pokemonRadioButton._1().requestFocus();
                pokemonRadioButton._1().setSelected(true);
                pokemonRadioButton._1().setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.BOLD, FONT_SIZE));
                JUtil.setFocus(pokemonRadioButton._1());
                first = false;
            }
        }
    }
}
