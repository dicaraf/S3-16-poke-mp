package view.game_menu;

import controller.GameController;
import controller.GameMenuController;
import model.entities.Owner;
import model.entities.PokemonFactory;
import model.entities.PokemonWithLife;
import model.entities.Trainer;
import scala.Tuple3;
import utilities.Settings;
import view.BasePanel;
import view.JUtil;
import view.LoadImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TeamPanel draws the trainer's Pokemons taking part to the team (the six favourite Pokemons of the trainer). It shows
 * just basic info about every Pokemon (icon, name, life and level). It allows to choose a Pokemon to see detailed infos
 * about it.
 */
public class TeamPanel extends BasePanel {
    private static final int FONT_SIZE = (int) (Settings.Constants$.MODULE$.FRAME_SIDE() * 0.034);
    private static final int iconSide = (int) (Settings.Constants$.MODULE$.FRAME_SIDE() * 0.1177);
    protected List<Tuple3<JRadioButton,PokemonWithLife,Integer>> buttonList = new ArrayList<>();
    private ButtonGroup pokemonButtonGroup = new ButtonGroup();

    /**
     * @param gameMenuController instance of GameMenuController
     * @param gameController instance of GameController
     * @param trainer instance of trainer
     */
    public TeamPanel(GameMenuController gameMenuController, GameController gameController, Trainer trainer) {
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.TEAM_PANEL_BACKGROUND());
        JLabel infoText = new JLabel(Settings.Strings$.MODULE$.TEAM_PANEL_INFO());
        this.downPanel.add(infoText, BorderLayout.CENTER);
        List pokemonList = scala.collection.JavaConverters.seqAsJavaList(trainer.favouritePokemons());
        Boolean first = true;
        Image myImage;
        ImageIcon myImageIcon = null;
        k.insets = new Insets(1,1,1,1);

        for(Object pokemon: pokemonList){
            if(Integer.parseInt(pokemon.toString()) != 0){
                final int pokemonId = Integer.parseInt(pokemon.toString());
                final PokemonWithLife pokemonWithLife = PokemonFactory
                        .createPokemon(Owner.TRAINER(), Optional.of(pokemonId), Optional.empty()).get();
                String s = pokemonWithLife.pokemon().name().toUpperCase() + "   Life: " + pokemonWithLife.pokemonLife() + "/" +
                        pokemonWithLife.pokemon().experiencePoints() + "   Lv:  " + pokemonWithLife.pokemon().level();
                try {
                    myImage = ImageIO.read(getClass().getResource(Settings.Images$.MODULE$.POKEMON_IMAGES_ICON_FOLDER() + pokemonWithLife.pokemon().id() + ".png"));
                    myImageIcon = new ImageIcon(myImage.getScaledInstance(iconSide,iconSide,java.awt.Image.SCALE_SMOOTH));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JRadioButton radioButton = new JRadioButton(s,myImageIcon);
                radioButton.setOpaque(false);
                radioButton.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, FONT_SIZE));
                radioButton.addItemListener(e -> {
                    JRadioButton button = (JRadioButton) e.getItem();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        button.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.BOLD, FONT_SIZE));
                    }
                    else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        button.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, FONT_SIZE));
                    }
                });
                radioButton.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
                            gameMenuController.showPokemonInTeamPanel(pokemonWithLife);
                        }
                    }
                });
                if(first){
                    radioButton.setSelected(true);
                    radioButton.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.BOLD, FONT_SIZE));
                    JUtil.setFocus(radioButton);
                    first = false;
                }
                JUtil.setEscClick(radioButton, this.backButton);
                buttonList.add(new Tuple3<>(radioButton,pokemonWithLife, pokemonId));
                pokemonButtonGroup.add(radioButton);
                this.centralPanel.add(radioButton, k);
                k.gridy++;
            }
        }

        if(pokemonButtonGroup.getButtonCount() == 0){
            JUtil.setFocus(this);
            JUtil.setEscClick(this, this.backButton);
        }

        this.backButton.addActionListener(e -> {
            gameController.resume();
            gameController.pause();
            gameMenuController.showGameMenu();
        });
    }
}
