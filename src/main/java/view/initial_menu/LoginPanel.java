package view.initial_menu;

import controller.LoginController;
import utilities.Settings;
import view.BasePanel;
import view.JUtil;
import view.LoadImage;

import javax.swing.*;
import java.awt.*;

/**
 * LoginPanel allows access the game
 */
public class LoginPanel extends BasePanel {
    private LoginController controller;
    private JButton submit;

    /**
     * @param controller instance of LoginController
     */
    public LoginPanel(LoginController controller) {
        this.controller = controller;
        JLabel usernameLabel = new JLabel(Settings.Strings$.MODULE$.USERNAME());
        JLabel passwordLabel = new JLabel(Settings.Strings$.MODULE$.PASSWORD());
        JTextField usernameField = new JTextField(20);
        JTextField passwordField = new JPasswordField(20);
        this.submit = new JButton(Settings.Strings$.MODULE$.SUBMIT_BUTTON());
        this.imagePanel = LoadImage.load(Settings.Images$.MODULE$.LOGIN_PANEL_BACKGROUND());
        this.backButton.addActionListener(e -> this.controller.back());
        usernameLabel.setForeground(Color.WHITE);
        passwordLabel.setForeground(Color.WHITE);

        this.centralPanel.add(usernameLabel, k);
        k.gridy++;
        this.centralPanel.add(usernameField, k);
        k.gridy++;
        this.centralPanel.add(passwordLabel, k);
        k.gridy++;
        this.centralPanel.add(passwordField, k);
        k.gridy++;
        this.centralPanel.add(submit,k);
        submit.addActionListener(e -> this.controller.login(usernameField.getText(), passwordField.getText()));
        JUtil.setFocus(usernameField);
        JUtil.setSubmitEnterClick(usernameField, submit);
        JUtil.setSubmitEnterClick(passwordField, submit);
        JUtil.setEnterClick(submit);
        JUtil.setEscClick(usernameField, this.backButton);
        JUtil.setEscClick(passwordField, this.backButton);
        JUtil.setEscClick(submit, this.backButton);
        JUtil.setEscClick(this.backButton, this.backButton);
        JUtil.setEnterClick(this.backButton);
    }

    public void changeLoginButton(String s) {
        this.submit.setText(s);
    }
}
