package org.example.lab6javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.lab6javafx.Service.SocialNetwork;
import org.example.lab6javafx.domain.Utilizator;

import java.io.IOException;
import java.net.URL;

public class SignUpController {
    private SocialNetwork socialNetworkService;
    private Stage stage;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private Button cancelButton;

    public void setSignUp(SocialNetwork socialNetworkService, Stage stage) {
        this.socialNetworkService = socialNetworkService;
        this.stage = stage;
    }

    @FXML
    public void handlePanelClicks(ActionEvent event) throws IOException {
        if (event.getSource() == signUpButton) {
            handleSignUp();
        } else if (event.getSource() == cancelButton) {
            handleCancel();
        }
    }

    private void handleCancel() {
        goBackToLogIn();
    }

    private void handleSignUp() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Warning", "All fields must be filled out!");
            return;
        }
        if(!passwordField.getText().equals(confirmPasswordField.getText())) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Warning", "Password is incorrect!");
            return;
        }
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        Utilizator user = socialNetworkService.findUserByUsername(username);

        if(user == null) {
            socialNetworkService.addUser(firstName, lastName, username, password);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "User added successfully!");
        } else {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Information", "User already exists!");
        }
        goBackToLogIn();
    }

    private void goBackToLogIn() {
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/login.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign Up");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LogInController logInController = fxmlLoader.getController();
            logInController.setLogIn(socialNetworkService, dialogStage);

            stage.close();
            dialogStage.show();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
