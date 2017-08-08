package view.dialogue;

import utilities.Settings;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Dialogue shown when the trainer wait the fight response from another trainer
 */
public class WaitingTrainerDialoguePanel extends DialoguePanel {

    /**
     * @param trainerName challenger character's name
     */
    public WaitingTrainerDialoguePanel(String trainerName) {
        super(new ArrayList<>(Collections.singletonList(Settings.Strings$.MODULE$.WAITING_TRAINER_LABEL() + trainerName)));
    }

    @Override
    protected void setFinalButtons() {
        buttonPanel.removeAll();
        buttons.clear();
        repaint();
    }
}
