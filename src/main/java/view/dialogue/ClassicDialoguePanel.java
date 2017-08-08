package view.dialogue;

import controller.GameController;
import utilities.Settings;
import view.JUtil;

import javax.swing.*;
import java.util.List;

/**
 * ClassicDialoguePanel is a dialogue which the trainer doesn't have to interact with
 */
public class ClassicDialoguePanel extends DialoguePanel {
    private GameController gameController;

    /**
     * @param gameController instance of GameController
     * @param dialogues character's dialogue
     */
    public ClassicDialoguePanel(final GameController gameController, final List<String> dialogues) {
        super(dialogues);
        this.gameController = gameController;
    }

    @Override
    protected void setFinalButtons() {
        buttonPanel.removeAll();
        buttons.clear();
        final JButton finalButton = new JButton(Settings.Strings$.MODULE$.FINAL_DIALOGUE_BUTTON());
        finalButton.addActionListener(e -> {
            gameController.setFocusableOn();
            this.setVisible(false);
        });
        buttonPanel.add(finalButton);
        finalButton.requestFocus();
        JUtil.setFocus(finalButton);
        repaint();
    }
}
