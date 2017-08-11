package view.dialogue;

import utilities.Settings;
import view.JUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * DialoguePanel shows the characters' dialogues in the main panel
 */
public abstract class DialoguePanel extends JPanel implements KeyListener{
    protected final JLabel dialogueLabel;
    protected int currentButton = 0;
    private int currentDialogue = 0;
    protected final List<JButton> buttons = new ArrayList<>();
    protected final JPanel buttonPanel = new JPanel();

    /**
     * @param dialogues character's dialogue
     */
    DialoguePanel(final List<String> dialogues){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createLoweredBevelBorder()));
        dialogueLabel = new JLabel("", SwingConstants.CENTER);
        dialogueLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        add(dialogueLabel, BorderLayout.CENTER);
        buttonPanel.setBackground(Color.WHITE);
        add(buttonPanel, BorderLayout.SOUTH);
        dialogueLabel.setText(dialogues.get(currentDialogue));
        if(dialogues.size() != 1){
            final JButton buttonNext = new JButton(Settings.Strings$.MODULE$.NEXT_DIALOGUE_BUTTON());
            buttonNext.addActionListener(e -> {
                if (dialogues.size() > currentDialogue) {
                    currentDialogue++;
                    dialogueLabel.setText(dialogues.get(currentDialogue));
                }
                if (dialogues.size() - 1 == currentDialogue) {
                    currentDialogue++;
                    setFinalButtons();
                }
            });
            buttons.add(buttonNext);
            buttonNext.addKeyListener(this);
            buttonPanel.add(buttonNext);
            buttonNext.requestFocus();
            JUtil.setFocus(buttonNext);
        }else{
            setFinalButtons();
        }
    }

    /**
     * Set the final button to end the dialogue
     */
    protected abstract void setFinalButtons();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                if (currentButton < buttons.size()-1) {
                    buttons.get(++currentButton).requestFocus();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (currentButton > 0) {
                    buttons.get(--currentButton).requestFocus();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
