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

public class LogInController {
    private SocialNetwork socialNetworkService;
    private Stage stage;

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    @FXML
    Button loginButton;

    @FXML
    Button signUpButton;

    @FXML
    Button cancelButton;

    public void setLogIn(SocialNetwork socialNetworkService, Stage stage) {
        this.socialNetworkService = socialNetworkService;
        this.stage = stage;
        this.stage.centerOnScreen();
    }


    @FXML
    private void handlePanelClicks(ActionEvent event) throws IOException {
        if(event.getSource() == loginButton) {
            handleLogIn();
        }else if(event.getSource() == signUpButton) {
            handleSignUp();
        }else if (event.getSource() == cancelButton) {
            stage.close();
        }
    }

    private void handleLogIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username.isEmpty() || password.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error!", "Fields can't be empty!");
            return;
        }
        Utilizator userFound = socialNetworkService.findUserByUsername(username);
        if(userFound == null) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error!", "The user does not exist!");
            return;
        } else if(userFound.getPassword().equals(password)) {
            try{
                URL resource = getClass().getResource("/org/example/lab6javafx/mainpage.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                AnchorPane root = fxmlLoader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Home");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                Utilizator user = socialNetworkService.findUserByUsername(username);
                MainPageController mainPageController = fxmlLoader.getController();
                mainPageController.setMainPage(socialNetworkService, dialogStage, user);

                dialogStage.show();
            }catch (IOException e){
                e.printStackTrace();
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error!", "A problem occurred while logging in!");
                throw new RuntimeException();
            }
        }else if(!userFound.getPassword().equals(password)) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error!", "The password is incorrect!");
            return;
        }
    }

    private void handleSignUp() throws IOException {
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/signup.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign Up");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SignUpController signUpController = fxmlLoader.getController();
            signUpController.setSignUp(socialNetworkService, dialogStage);

            stage.close();
            dialogStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
