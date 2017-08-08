package view;

import controller.BuildingController;
import model.entities.Owner;
import model.entities.PokemonFactory;
import model.entities.PokemonWithLife;
import model.environment.Audio;
import model.environment.AudioImpl;
import scala.Tuple2;
import utilities.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * BoxPanel allows to change favorite pokemon (team) with captured pokemon
 */
class BoxPanel extends ImagePanel {
    private static final int iconSide = (int) (Settings.Constants$.MODULE$.FRAME_SIDE() * 0.1);
    private final JPanel teamPanel;
    private final JPanel boxPanel;
    private final JLabel teamLabel = new JLabel();
    private final JLabel boxLabel = new JLabel();
    private final PokemonPanel pokemonPanel;
    private List<Object> favoritePokemon;
    private List<Tuple2<Object, Object>> capturedPokemon;
    private Audio audio;

    /**
     * @param buildingController instance of BuildingController
     */
    BoxPanel(BuildingController buildingController){
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.BOX_PANEL_BACKGROUND());
        audio = new AudioImpl(Settings.Audio$.MODULE$.MENU_SONG());
        audio.play();
        this.favoritePokemon = new ArrayList<>();
        List<Object> favoritePokemon = scala.collection.JavaConverters.seqAsJavaList(buildingController.trainer().favouritePokemons());
        this.favoritePokemon = new ArrayList<>();
        this.favoritePokemon.addAll(favoritePokemon);
        this.capturedPokemon = scala.collection.JavaConverters.seqAsJavaList(buildingController.trainer().capturedPokemons());
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Pokémon Box");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title, BorderLayout.NORTH);
        GridLayout gridLayout;
        if(Settings.Constants$.MODULE$.FRAME_SIDE() > 530) {
            gridLayout = new GridLayout(0,3);
        } else {
            gridLayout = new GridLayout(0,2);
        }
        teamPanel = new JPanel(gridLayout);
        boxPanel = new JPanel(gridLayout);

        pokemonPanel = new PokemonPanel();

        final JScrollPane scrollFrame = new JScrollPane(boxPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        boxPanel.setAutoscrolls(true);

        final JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(teamLabel, BorderLayout.NORTH);
        leftPanel.add(teamPanel, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(Settings.Constants$.MODULE$.FRAME_SIDE()/4, 0));
        add(leftPanel, BorderLayout.WEST);

        final JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(boxLabel, BorderLayout.NORTH);
        rightPanel.add(scrollFrame, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension((int)(Settings.Constants$.MODULE$.FRAME_SIDE()/3.5), 0));
        add(rightPanel, BorderLayout.EAST);

        add(pokemonPanel, BorderLayout.CENTER);
        pokemonPanel.setVisible(false);
        final JButton close = new JButton("SAVE AND CLOSE");
        close.addActionListener(e -> {
            buildingController.trainer().setAllFavouritePokemon(this.favoritePokemon);
            audio.stop();
            buildingController.resume();
        });
        add(close, BorderLayout.SOUTH);

        paintBox();
    }

    /**
     * Refresh the box when the user change pokemon
     */
    private void paintBox() {
        teamPanel.removeAll();
        boxPanel.removeAll();

        for(Object pokemonObject: this.favoritePokemon) {
            int pokemonId = Integer.parseInt(pokemonObject.toString());
            if(pokemonId != 0) {
                final PokemonWithLife pokemonWithLife = PokemonFactory
                        .createPokemon(Owner.TRAINER(), Optional.of(pokemonId), Optional.empty()).get();
                final JLabel pokemonImage = new JLabel(getPokemonIcon(pokemonWithLife.pokemon().imageName()));
                final JLabel pokemonLabel = new JLabel("Lv." + pokemonWithLife.pokemon().level());
                pokemonLabel.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(),Font.PLAIN,Settings.Constants$.MODULE$.FRAME_SIDE()/45));
                pokemonImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                pokemonImage.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        pokemonPanel.setPokemon(pokemonWithLife);
                        if (!pokemonPanel.isVisible()) pokemonPanel.setVisible(true);
                    }

                });
                teamPanel.add(pokemonImage);
                if(Settings.Constants$.MODULE$.FRAME_SIDE() > 530) teamPanel.add(pokemonLabel);
                final JButton button = new JButton(">>");
                teamPanel.add(button);
                button.addActionListener(e -> {
                    if (Collections.frequency(this.favoritePokemon, 0) < favoritePokemon.size() - 1) {
                        this.favoritePokemon.set(this.favoritePokemon.indexOf(pokemonId), 0);
                        paintBox();
                    }
                });
            }
        }

        for(Tuple2<Object, Object> pokemonObject: this.capturedPokemon) {
            int pokemonId = Integer.parseInt(pokemonObject._1().toString());
            final PokemonWithLife pokemonWithLife = PokemonFactory
                    .createPokemon(Owner.TRAINER(), Optional.of(pokemonId), Optional.empty()).get();
            if(!this.favoritePokemon.contains(pokemonId)){
                final JButton button = new JButton("<<");
                final JLabel pokemonImage = new JLabel(getPokemonIcon(pokemonWithLife.pokemon().imageName()));
                final JLabel pokemonLabel = new JLabel("Lv."+pokemonWithLife.pokemon().level());
                pokemonLabel.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(),Font.PLAIN,Settings.Constants$.MODULE$.FRAME_SIDE()/45));
                pokemonImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                pokemonImage.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        pokemonPanel.setPokemon(pokemonWithLife);
                        if(!pokemonPanel.isVisible()) pokemonPanel.setVisible(true);
                    }
                });
                boxPanel.add(button);
                boxPanel.add(pokemonImage);
                if(Settings.Constants$.MODULE$.FRAME_SIDE() > 530) boxPanel.add(pokemonLabel);
                button.addActionListener(e ->{
                    if(this.favoritePokemon.contains(0)){
                        this.favoritePokemon.set(this.favoritePokemon.indexOf(0), pokemonId);
                        paintBox();
                    }
                });
            }
        }
        refreshTeamAndBoxNumber();
        revalidate();
        repaint();
    }

    /**
     * Refresh the number of favorite pokemon and all other pokemon in the box
     */
    private void refreshTeamAndBoxNumber() {
        teamLabel.setText("TEAM "+ (this.favoritePokemon.size() - Collections.frequency(this.favoritePokemon, 0)) +"/"+favoritePokemon.size());
        boxLabel.setText("BOX: "+(this.capturedPokemon.size() - (this.favoritePokemon.size() - Collections.frequency(this.favoritePokemon, 0)))+" Pokemon");
    }

    /**
     * Get the image icon of a pokemon
     * @param pokemonImageString path of the pokemon image
     * @return ImageIcon of the pokemon
     */
    private ImageIcon getPokemonIcon(String pokemonImageString) {
        Image myImage;
        ImageIcon myImageIcon = null;
        try {
            myImage = ImageIO.read(getClass().getResource(Settings.Images$.MODULE$.POKEMON_IMAGES_ICON_FOLDER() + pokemonImageString));
            myImageIcon = new ImageIcon(myImage.getScaledInstance(iconSide,iconSide,java.awt.Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myImageIcon;
    }

}
