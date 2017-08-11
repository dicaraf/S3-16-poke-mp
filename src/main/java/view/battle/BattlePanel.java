package view.battle;

import controller.BattleController;
import database.local.PokedexConnect;
import model.entities.Owner;
import model.entities.PokemonWithLife;
import utilities.Settings;
import view.ImagePanel;
import view.LoadImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class BattlePanel extends ImagePanel implements BattleView {
    private final static int IMAGE_POKEMON_SIZE = Settings.Constants$.MODULE$.FRAME_SIDE() / 4;
    private final static int FONT_SIZE = (int)(Settings.Constants$.MODULE$.FRAME_SIDE() * 0.034);
    private final static int FONT_LIFE_SIZE = (int)(Settings.Constants$.MODULE$.FRAME_SIDE() * 0.02);
    private final static int PROGRESS_BAR_W = (int)(Settings.Constants$.MODULE$.FRAME_SIDE() * 0.23437);
    private final static int PROGRESS_BAR_H = (int)(Settings.Constants$.MODULE$.FRAME_SIDE() * 0.021648);
    private final static Dimension[] POKEMON_IMG_POSE = {newDimension(0.65,0.10),newDimension(0.11,0.48)};
    private final static Dimension[] POKEMON_NAME_POSE = {newDimension(0.08,0.165),newDimension(0.57,0.565)};
    private final static Dimension[] POKEMON_LEVEL_POSE = {newDimension(0.06,0.205),newDimension(0.55,0.61)};
    private final static Dimension[] POKEMON_PROGRESS_BAR_POSE = {newDimension(0.214,0.2155),newDimension(0.7,0.613)};
    private final static Dimension POKEMON_LIFE_POSE = newDimension(0.845,0.625);
    private final static Dimension POKEMON_LEV_EXP_POSE = newDimension(0.64,0.666);
    private PokemonWithLife[] pokemonEntities;
    private JButton[] pokemonImages = new JButton[]{new JButton(),new JButton()};
    private String[] pokemonSide = new String[]{Settings.Images$.MODULE$.POKEMON_IMAGES_FRONT_FOLDER(),Settings.Images$.MODULE$.POKEMON_IMAGES_BACK_FOLDER()};
    private JTextField[] pokemonNames = new JTextField[]{new JTextField(),new JTextField()};
    private JTextField[] pokemonLevels = new JTextField[]{new JTextField(),new JTextField()};
    private JProgressBar[] pokemonProgressBar = new JProgressBar[]{new JProgressBar(0,100),new JProgressBar(0,100)};
    private JTextField myPokemonLife = new JTextField();
    private JProgressBar pokemonLevExpBar = new JProgressBar(0, 100);
    private Map<String,JButton> trainerChoices = new LinkedHashMap<>();
    private String[] myPokemonAttacks = new String[4];
    private String[] wildPokemonAttacks;
    private Map<String,JButton> attacks = new LinkedHashMap<>();
    private int index = 0;
    private boolean attacksAreVisible = false;
    private boolean pokeballAnimation = false;
    private boolean pokemonIsInThePokeball = false;
    private Timer t;
    private int pokeballX = 0;
    private int pokeballY = 0;
    private Image pokeballImage = LoadImage.load(Settings.Images$.MODULE$.POKEBALL_IMAGE());
    private JPanel displayPanel = new JPanel(new BorderLayout());
    private JLabel attackExplanation = new JLabel();
    private BattleController controller;

    public BattlePanel(PokemonWithLife myPokemon, PokemonWithLife otherPokemon, JFrame frame, BattleController controller) {
        this.controller = controller;
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.BATTLE_PANEL_BACKGROUND());
        this.pokemonEntities = new PokemonWithLife[]{otherPokemon, myPokemon};
        this.setLayout(null);
        this.setFocusable(true);
        this.requestFocusInWindow();
        createBattleGraphic();
        createTrainerChoicesGraphic();
        catchTrainerChoices(frame);
    }

    private void createBattleGraphic() {
        for(int i=0;i<pokemonEntities.length;i++) {
            createJTextField(pokemonNames[i],pokemonEntities[i].pokemon().name().toUpperCase());
            pokemonNames[i].setBounds(POKEMON_NAME_POSE[i].width,POKEMON_NAME_POSE[i].height,200,30);
            pokemonNames[i].setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.BOLD, FONT_SIZE));
            createJTextField(pokemonLevels[i],"L."+pokemonEntities[i].pokemon().level());
            pokemonLevels[i].setBounds(POKEMON_LEVEL_POSE[i].width,POKEMON_LEVEL_POSE[i].height,70,30);
            pokemonLevels[i].setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, FONT_SIZE));
            Image myImage;
            ImageIcon myImageIcon = null;
            try {
                myImage = ImageIO.read(getClass().getResource(pokemonSide[i] + pokemonEntities[i].pokemon().id() + ".png"));
                myImageIcon = new ImageIcon(myImage.getScaledInstance(IMAGE_POKEMON_SIZE,IMAGE_POKEMON_SIZE,java.awt.Image.SCALE_SMOOTH));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pokemonImages[i].setIcon(myImageIcon);
            pokemonImages[i].setBounds(POKEMON_IMG_POSE[i].width,POKEMON_IMG_POSE[i].height,IMAGE_POKEMON_SIZE,IMAGE_POKEMON_SIZE);
            pokemonImages[i].setBorder(null);
            pokemonImages[i].setContentAreaFilled(false);
            this.add(pokemonImages[i]);
            pokemonProgressBar[i].setBounds(POKEMON_PROGRESS_BAR_POSE[i].width,POKEMON_PROGRESS_BAR_POSE[i].height,PROGRESS_BAR_W,PROGRESS_BAR_H);
            pokemonProgressBar[i].setMaximum(pokemonEntities[i].pokemon().experiencePoints());
            pokemonProgressBar[i].setValue(pokemonEntities[i].pokemonLife());
            this.add(pokemonProgressBar[i]);
        }
        createJTextField(myPokemonLife,pokemonEntities[1].pokemonLife()+"/"+pokemonEntities[1].pokemon().experiencePoints());
        myPokemonLife.setBounds(POKEMON_LIFE_POSE.width,POKEMON_LIFE_POSE.height,100,30);
        myPokemonLife.setFont(new Font(Settings.Constants$.MODULE$.FONT_NAME(), Font.PLAIN, FONT_LIFE_SIZE));
        this.add(myPokemonLife);
        pokemonLevExpBar.setValue(pokemonEntities[1].pokemon().levelExperience());
        pokemonLevExpBar.setBounds(POKEMON_LEV_EXP_POSE.width,POKEMON_LEV_EXP_POSE.height,170,8);
        this.add(pokemonLevExpBar);
        getWildPokemonAttack();
    }

    private void createTrainerChoicesGraphic() {
        displayPanel.setBounds(Settings.Constants$.MODULE$.FRAME_SIDE()/40,(int)(Settings.Constants$.MODULE$.FRAME_SIDE()*0.72),(int)(Settings.Constants$.MODULE$.FRAME_SIDE()*0.9),Settings.Constants$.MODULE$.FRAME_SIDE()/5);
        displayPanel.setBackground(Color.white);
        attackExplanation.setHorizontalAlignment(JLabel.CENTER);
        displayPanel.add(attackExplanation,BorderLayout.CENTER);
        this.add(displayPanel);
        displayPanel.setVisible(false);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBounds(Settings.Constants$.MODULE$.FRAME_SIDE()/40,(int)(Settings.Constants$.MODULE$.FRAME_SIDE()*0.72),(int)(Settings.Constants$.MODULE$.FRAME_SIDE()*0.9),Settings.Constants$.MODULE$.FRAME_SIDE()/5);
        southPanel.setOpaque(false);
        JPanel southWestPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(4,1);
        gridLayout.setVgap(10);
        southWestPanel.setLayout(gridLayout);
        southWestPanel.setOpaque(false);
        trainerChoices.put("Attack",new JButton("Attack"));
        trainerChoices.put("Change pokemon",new JButton("Change Pokemon"));
        trainerChoices.put("Pokeball",new JButton("Pokeball (x"+controller.getPokeballAvailableNumber()+")"));
        trainerChoices.put("Escape",new JButton("Escape"));
        for(String s : trainerChoices.keySet()) {
            trainerChoices.get(s).setFocusable(false);
            southWestPanel.add(trainerChoices.get(s));
        }
        if(controller.isDistributedBattle()) {
            trainerChoices.get("Pokeball").setEnabled(false);
            trainerChoices.get("Escape").setEnabled(false);
            if(!controller.yourPlayerIsFirst()) {
                blockButtons(true);
            }
        }
        JPanel southEastPanel = new JPanel();
        gridLayout = new GridLayout(2,2);
        gridLayout.setVgap(10);
        gridLayout.setHgap(10);
        southEastPanel.setLayout(gridLayout);
        southEastPanel.setBounds(Settings.Constants$.MODULE$.FRAME_SIDE()/35,(int)(Settings.Constants$.MODULE$.FRAME_SIDE()*0.72),(int)Settings.Constants$.MODULE$.FRAME_SIDE(),Settings.Constants$.MODULE$.FRAME_SIDE()/5);
        southEastPanel.setOpaque(false);
        int[] pokemonAttacks = {(int)pokemonEntities[1].pokemon().attacks()._1(),(int)pokemonEntities[1].pokemon().attacks()._2(),
                (int)pokemonEntities[1].pokemon().attacks()._3(),(int)pokemonEntities[1].pokemon().attacks()._4()};
        for(int i=0;i<4;i++) {
            this.myPokemonAttacks[i] = PokedexConnect.getPokemonAttack(pokemonAttacks[i]).get()._1().toUpperCase();
            attacks.put(myPokemonAttacks[i],new JButton(myPokemonAttacks[i]));
            attacks.get(myPokemonAttacks[i]).setFocusable(false);
            southEastPanel.add(attacks.get(myPokemonAttacks[i]));
            attacks.get(myPokemonAttacks[i]).setVisible(false);
        }
        southPanel.add(southWestPanel,BorderLayout.WEST);
        southPanel.add(southEastPanel,BorderLayout.CENTER);
        this.add(southPanel);
    }

    private void catchTrainerChoices(JFrame frame) {
        frame.getRootPane().setDefaultButton(trainerChoices.get(trainerChoices.entrySet().iterator().next().getKey()));
        trainerChoices.get("Attack").addActionListener(e -> {
            attacksAreVisible = true;
            changeButtons();
            index = 0;
            Object[] names = attacks.keySet().toArray();
            frame.getRootPane().setDefaultButton(attacks.get(names[index].toString()));
        });
        trainerChoices.get("Change pokemon").addActionListener(e -> controller.changePokemon());
        trainerChoices.get("Pokeball").addActionListener(e -> {
            trainerChoices.get("Pokeball").setEnabled(false);
            pokeballAnimation = true;
            pokemonIsInThePokeball = false;
            pokeballImage = LoadImage.load(Settings.Images$.MODULE$.POKEBALL_IMAGE());
            pokeballX = (int)(POKEMON_IMG_POSE[1].width+Settings.Constants$.MODULE$.FRAME_SIDE()*0.1);
            pokeballY = POKEMON_IMG_POSE[1].height;
            t = new Timer(10,(ActionEvent ex) -> {
                if(pokeballY > POKEMON_IMG_POSE[0].height && !pokemonIsInThePokeball){
                    pokeballX += 5;
                    pokeballY -= 5;
                } else if (pokeballY <= POKEMON_IMG_POSE[0].height && !pokemonIsInThePokeball) {
                    pokeballImage = LoadImage.load(Settings.Images$.MODULE$.POKEBALL_OPEN_IMAGE());
                    pokemonIsInThePokeball = true;
                    Thread animation = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(1000);
                                pokeballImage = LoadImage.load(Settings.Images$.MODULE$.POKEBALL_IMAGE());
                                pokemonImages[0].setVisible(false);
                                Thread.sleep(1500);
                                if(controller.trainerThrowPokeball()) {
                                    pokeballImage = LoadImage.load(Settings.Images$.MODULE$.POKEBALL_RED_IMAGE());
                                    Thread.sleep(1000);
                                } else {
                                    pokeballImage = LoadImage.load(Settings.Images$.MODULE$.POKEBALL_OPEN_IMAGE());
                                    pokemonImages[0].setVisible(true);
                                    Thread.sleep(1000);
                                    pokeballAnimation = false;
                                    trainerChoices.get("Pokeball").setText("Pokeball (x"+controller.getPokeballAvailableNumber()+")");
                                    if(controller.getPokeballAvailableNumber()>0) {
                                        trainerChoices.get("Pokeball").setEnabled(true);
                                    }
                                }
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    };
                    animation.start();
                }
                repaint();
            });
            t.start();
        });
        trainerChoices.get("Escape").addActionListener(e -> controller.trainerCanQuit());
        for(String att : attacks.keySet()) {
            attacks.get(att).addActionListener(e -> {
                Object[] atts = attacks.keySet().toArray();
                for(int j=0;j<4;j++){
                    if(atts[j].toString().toUpperCase() == att) {
                        switch (j) {
                            case 0 :
                                controller.myPokemonAttacks((int)pokemonEntities[1].pokemon().attacks()._1());
                                break;
                            case 1 :
                                controller.myPokemonAttacks((int)pokemonEntities[1].pokemon().attacks()._2());
                                break;
                            case 2 :
                                controller.myPokemonAttacks((int)pokemonEntities[1].pokemon().attacks()._3());
                                break;
                            case 3 :
                                controller.myPokemonAttacks((int)pokemonEntities[1].pokemon().attacks()._4());
                                break;
                        }
                    }
                }
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            attacksAreVisible = false;
                            changeButtons();
                            displayPanel.setVisible(true);
                            attackExplanation.setText(pokemonEntities[1].pokemon().name().toUpperCase()+" USES " + att +"!");
                            if(controller.isDistributedBattle()) {
                                blockButtons(true);
                                for(String c : trainerChoices.keySet()) {
                                    trainerChoices.get(c).setVisible(false);
                                }
                                Thread.sleep(3000);
                                attackExplanation.setText("WAITING YOUR OPPONENT'S MOVE");
                            } else {
                                Thread.sleep(3000);
                                attackExplanation.setText(pokemonEntities[0].pokemon().name().toUpperCase()+" USES " +
                                        wildPokemonAttacks[new Random().nextInt(wildPokemonAttacks.length)].toUpperCase()+"!");
                                Thread.sleep(3000);
                                displayPanel.setVisible(false);
                            }
                            index = 0;
                            Object[] names = trainerChoices.keySet().toArray();
                            frame.getRootPane().setDefaultButton(trainerChoices.get(names[index].toString()));
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                };
                t.start();
            });
        }
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if(index<trainerChoices.size()-1){
                            index++;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if(index>0) {
                            index--;
                        }
                        break;
                    default: break;
                }
                if(!attacksAreVisible) {
                    Object[] choices = trainerChoices.keySet().toArray();
                    frame.getRootPane().setDefaultButton(trainerChoices.get(choices[index].toString()));
                } else {
                    Object[] names = attacks.keySet().toArray();
                    frame.getRootPane().setDefaultButton(attacks.get(names[index].toString()));
                }

            }
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    private static Dimension newDimension(Double d1, Double d2) {
        return new Dimension((int)(Settings.Constants$.MODULE$.FRAME_SIDE()*d1),(int)(Settings.Constants$.MODULE$.FRAME_SIDE()*d2));
    }
    private void createJTextField(JTextField jTextField,String text) {
        jTextField.setText(text);
        jTextField.setOpaque(false);
        jTextField.setBorder(null);
        jTextField.setEnabled(false);
        jTextField.setDisabledTextColor(Color.black);
        this.add(jTextField);
    }
    private void changeButtons() {
        for(String c : trainerChoices.keySet()) {
            trainerChoices.get(c).setVisible(!attacksAreVisible);
        }
        for(String att : attacks.keySet()) {
            attacks.get(att).setVisible(attacksAreVisible);
        }
    }
    private void blockButtons(boolean flag) {
        if(pokemonEntities[1].pokemonLife()!=0){
            for(String c : trainerChoices.keySet()) {
                trainerChoices.get(c).setEnabled(!flag);
            }
            trainerChoices.get("Pokeball").setEnabled(false);
            trainerChoices.get("Escape").setEnabled(false);
            for(String att : attacks.keySet()) {
                attacks.get(att).setEnabled(!flag);
            }
        }
    }
    private void getWildPokemonAttack() {
        wildPokemonAttacks = new String[]{PokedexConnect.getPokemonAttack((int) pokemonEntities[0].pokemon().attacks()._1()).get()._1(),
                PokedexConnect.getPokemonAttack((int) pokemonEntities[0].pokemon().attacks()._2()).get()._1(),
                PokedexConnect.getPokemonAttack((int) pokemonEntities[0].pokemon().attacks()._3()).get()._1(),
                PokedexConnect.getPokemonAttack((int) pokemonEntities[0].pokemon().attacks()._4()).get()._1()};
    }
    /**
     * @inheritdoc
     */
    @Override
    public void setPokemonLife() {
        myPokemonLife.setText(pokemonEntities[1].pokemonLife()+"/"+pokemonEntities[1].pokemon().experiencePoints());
        if(controller.isDistributedBattle()) {
            blockButtons(false);
            for(String c : trainerChoices.keySet()) {
                trainerChoices.get(c).setVisible(true);
            }
            displayPanel.setVisible(false);
        }
    }
    /**
     * @inheritdoc
     */
    @Override
    public void setPokemonLifeProgressBar(int life, int owner) {
        if (owner == Owner.TRAINER().id()) { pokemonProgressBar[1].setValue(life); }
        else { pokemonProgressBar[0].setValue(life); }
    }
    /**
     * @inheritdoc
     */
    @Override
    public void pokemonIsDead(int i) {
        pokemonImages[i].setVisible(false);
    }
    /**
     * @inheritdoc
     */
    @Override
    public void pokemonWildAttacksAfterTrainerChoice() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    displayPanel.setVisible(true);
                    int i = new Random().nextInt(wildPokemonAttacks.length);
                    attackExplanation.setText(pokemonEntities[0].pokemon().name().toUpperCase() + " USES " +
                        wildPokemonAttacks[i].toUpperCase() + "!");
                    Thread.sleep(3000);
                    displayPanel.setVisible(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
    /**
     * @inheritdoc
     */
    @Override
    public int[] getOtherPokemonAttacks() { return new int[]{(Integer)pokemonEntities[0].pokemon().attacks()._1(),
            (Integer)pokemonEntities[0].pokemon().attacks()._2(), (Integer)pokemonEntities[0].pokemon().attacks()._3(),
            (Integer)pokemonEntities[0].pokemon().attacks()._4()};
    }
    /**
     * @inheritdoc
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.requestFocusInWindow();
        if(pokeballAnimation){
            g.drawImage(pokeballImage,pokeballX,pokeballY,this);
        }
    }
}
