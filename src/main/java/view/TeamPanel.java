package view;

import controller.BattleController;
import controller.GameViewObserver;
import database.remote.DBConnect;
import model.entities.Owner;
import model.entities.PokemonFactory;
import model.entities.PokemonWithLife;
import model.entities.Trainer;
import scala.Tuple2;
import utilities.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeamPanel extends BasePanel{
    private static final int FONT_SIZE = (int) (Settings.FRAME_SIDE() * 0.034);
    private static final int iconSide = (int) (Settings.FRAME_SIDE() * 0.1177);
    private static final int infoSide = (int) (Settings.FRAME_SIDE() * 0.05);
    ButtonGroup pokemonButtonGroup = new ButtonGroup();

    public TeamPanel(Trainer trainer, GameViewObserver gameController) {
        this.imagePanel = LoadImage.load(Settings.PANELS_FOLDER() + "pokemon-choice.png");
        Image myImage;
        ImageIcon myImageIcon = null;
        try {
            myImage = ImageIO.read(getClass().getResource(Settings.IMAGES_FOLDER() + "info.png"));
            myImageIcon = new ImageIcon(myImage.getScaledInstance(infoSide,infoSide,java.awt.Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List pokemonList = scala.collection.JavaConverters.seqAsJavaList(trainer.favouritePokemons());
        Boolean first = true;
        k.insets = new Insets(1,1,1,1);
        for(Object pokemon: pokemonList){
            if(Integer.parseInt(pokemon.toString()) != 0){
                final int pokemonId = Integer.parseInt(pokemon.toString());
                final PokemonWithLife pokemonWithLife = PokemonFactory
                        .createPokemon(Owner.TRAINER(), Optional.of(pokemonId), Optional.empty()).get();
                String s = pokemonWithLife.pokemon().name().toUpperCase() + "   Life: " + pokemonWithLife.pokemonLife() + "/" +
                        pokemonWithLife.pokemon().experiencePoints() + "   Lv:  " + pokemonWithLife.pokemon().level();
                try {
                    myImage = ImageIO.read(getClass().getResource(Settings.POKEMON_IMAGES_ICON_FOLDER() + pokemonWithLife.pokemon().id() + ".png"));
                    myImageIcon = new ImageIcon(myImage.getScaledInstance(iconSide,iconSide,java.awt.Image.SCALE_SMOOTH));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JRadioButton radioButton = new JRadioButton(s,myImageIcon);
                radioButton.setOpaque(false);
                radioButton.setFont(new Font("Verdana", Font.PLAIN, FONT_SIZE));
                radioButton.addItemListener(e -> {
                    JRadioButton button = (JRadioButton) e.getItem();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        button.setFont(new Font("Verdana", Font.BOLD, FONT_SIZE));
                    }
                    else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        button.setFont(new Font("Verdana", Font.PLAIN, FONT_SIZE));
                    }
                });
                radioButton.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            gameController.showPokemonInTeamPanel(pokemonWithLife);
                        }
                    }
                });
                if(first){
                    radioButton.requestFocus();
                    radioButton.setSelected(true);
                    radioButton.setFont(new Font("Verdana", Font.BOLD, FONT_SIZE));
                    radioButton.addAncestorListener(new AncestorListener() {
                        @Override
                        public void ancestorAdded(AncestorEvent ae) {
                            radioButton.requestFocus();
                        }

                        @Override
                        public void ancestorRemoved(AncestorEvent event) { }

                        @Override
                        public void ancestorMoved(AncestorEvent event) { }
                    });
                    first = false;
                }
                pokemonButtonGroup.add(radioButton);
                this.centralPanel.add(radioButton, k);
                k.gridy++;
            }
        }

        this.backButton.addActionListener(e -> {
            gameController.resumeGame();
            gameController.gamePanel().setFocusable(true);
        });
    }
}
