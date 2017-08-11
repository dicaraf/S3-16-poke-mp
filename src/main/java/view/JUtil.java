package view;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * JUtil allows you to perform focus operations avoiding code repetition in different JPanel classes
 */
public class JUtil {

    /**
     * Set focus to jComponent
     * @param jComponent
     */
    public static void setFocus(JComponent jComponent){
        jComponent.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                jComponent.requestFocus();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {

            }

            @Override
            public void ancestorMoved(AncestorEvent event) {

            }
        });
    }

    /**
     * Set enter click to jButton
     * @param jButton
     */
    public static void setEnterClick(AbstractButton jButton){
        jButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    jButton.doClick();
                }
            }
        });
    }

    /**
     * Set enter click to jButton when focus is on jComponent
     * @param jComponent
     * @param jButton
     */
    public static void setSubmitEnterClick(JComponent jComponent, AbstractButton jButton){
        jComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    jButton.doClick();
                }
            }
        });
    }

    /**
     * Set ESC click to jButton when focus is on jComponent
     * @param jComponent
     * @param jButton
     */
    public static void setEscClick(JComponent jComponent, AbstractButton jButton){
        jComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    jButton.doClick();
                }
            }
        });
    }
}
