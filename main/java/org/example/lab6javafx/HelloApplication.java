package org.example.lab6javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.lab6javafx.Service.SocialNetwork;
import org.example.lab6javafx.controller.LogInController;
import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.Message;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.domain.validators.FriendshipValidator;
import org.example.lab6javafx.domain.validators.UtilizatorValidator;
import org.example.lab6javafx.domain.validators.ValidationException;
import org.example.lab6javafx.domain.validators.Validator;
import org.example.lab6javafx.repository.AbstractRepository;
import org.example.lab6javafx.repository.Repository;
import org.example.lab6javafx.repository.database.FriendshipDBRepository;
import org.example.lab6javafx.repository.database.MessageDBRepository;
import org.example.lab6javafx.repository.database.UserDBRepository;
import org.example.lab6javafx.repository.database.UserPaging;

import java.io.IOException;

public class HelloApplication extends Application {
    SocialNetwork service;
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Reading data from database");
        String username="postgres";
        String pasword="0000";
        String url="jdbc:postgresql://localhost:5432/postgres";
        UserPaging utilizatorRepository =
                new UserDBRepository(url,username, pasword);
        AbstractRepository<Long, Friendship> friendshipRepository =
                new FriendshipDBRepository(url, username, pasword, new FriendshipValidator());
        AbstractRepository<Long, Message> repositoryMessage =
                new MessageDBRepository(url, username, pasword);
        service = new SocialNetwork(utilizatorRepository, friendshipRepository, repositoryMessage);

        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/org/example/lab6javafx/login.fxml"));
        AnchorPane setLayout = stageLoader.load();
        primaryStage.setTitle("Social Network");
        primaryStage.setScene(new Scene(setLayout, Color.POWDERBLUE));

        LogInController logInController = stageLoader.getController();
        logInController.setLogIn(service, primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}