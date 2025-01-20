package org.example.lab6javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.lab6javafx.Service.SocialNetwork;
import org.example.lab6javafx.domain.Message;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.utils.events.EntityChangeEvent;
import org.example.lab6javafx.utils.observer.Observer;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageController implements Observer<EntityChangeEvent> {
    private SocialNetwork socialNetworkService;
    private Stage stage;
    private Utilizator currentUser;
    private Utilizator friendSelected;

    @FXML
    private ObservableList<Message> modelMessages = FXCollections.observableArrayList();

    @FXML
    private TextField messageField;

    @FXML
    private ListView<Message> chatListView;

    @FXML
    private Button btnSendMessage; // Buton pentru trimiterea mesajului

    @FXML
    private Button btnReplyMessage; // Buton pentru răspuns

    @FXML
    private Button btnBack;

    public void setMessage(SocialNetwork socialNetworkService, Stage stage, Utilizator currentUser, Utilizator friendSelected) {
        this.socialNetworkService = socialNetworkService;
        this.socialNetworkService.addObserver(this);
        this.stage = stage;
        this.currentUser = currentUser;
        this.friendSelected = friendSelected;

        initModelMessages(currentUser, friendSelected);
    }

    private void initModelMessages(Utilizator user1, Utilizator user2) {
        if (user1 == null || user2 == null) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Invalid users!");
            return;
        }

        //// Obtinem mesajele dintre cei doi utilizatori
        Iterable<Message> messages = socialNetworkService.getMessagesBetweenUsers(user1, user2);
        List<Message> messageList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());


        modelMessages.setAll(messageList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


        chatListView.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    Label messageLabel = new Label(message.getMessage());
                    messageLabel.setWrapText(true);
                    messageLabel.setMaxWidth(170);
                    messageLabel.setStyle("-fx-font-size: 14px;");

                    Label dateLabel = new Label(message.getDate().format(formatter));
                    dateLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 10px;");
                    dateLabel.setAlignment(Pos.CENTER_RIGHT);


                    VBox messageVBox = new VBox(messageLabel, dateLabel);
                    messageVBox.setSpacing(2);
                    messageVBox.setAlignment(Pos.CENTER_RIGHT);
                    messageVBox.setStyle("-fx-background-radius: 10; -fx-padding: 5 10 5 10;");


                    if (message.getReplyingTo() != -1) {
                        Message originalMessage = socialNetworkService.getAllMessages().stream()
                                .filter(x -> x.getId().equals(message.getReplyingTo()))
                                .findFirst()
                                .orElse(null);

                        if (originalMessage != null) {
                            Label originalMessageLabel = new Label("Reply to: " + originalMessage.getMessage());
                            originalMessageLabel.setWrapText(true);
                            originalMessageLabel.setMaxWidth(150);
                            originalMessageLabel.setStyle("-fx-font-size: 12px;");

                            VBox replyVBox = new VBox(originalMessageLabel);
                            replyVBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8; -fx-padding: 5 10 5 10;");
                            replyVBox.setSpacing(2);
                            replyVBox.setAlignment(Pos.CENTER_LEFT);

                            messageVBox.getChildren().add(0, replyVBox); // Adăugăm răspunsul la începutul VBox-ului
                        }
                    }

                    if (message.getFrom().equals(currentUser)) {
                        messageVBox.setStyle(messageVBox.getStyle() + "-fx-background-color: #ff5a98; -fx-text-fill: #FFFFFF;");
                    } else {
                        messageVBox.setStyle(messageVBox.getStyle() + "-fx-background-color: #f8cbdb; -fx-text-fill: #FFFFFF;");
                    }


                    HBox hbox = new HBox(messageVBox);
                    hbox.setSpacing(10);
                    hbox.setAlignment(message.getFrom().equals(currentUser) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                    setGraphic(hbox);
                }
            }
        });

        chatListView.setItems(modelMessages);
    }



    @FXML
    private void handleUserPanelClick(ActionEvent event) {
        if (event.getSource() == btnSendMessage) {
            handleSendMessage(friendSelected);
        } else if (event.getSource() == btnReplyMessage) {
            if (getSelectedMessage() == null || messageField.getText().isEmpty()) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "No message selected or message is empty!");
            } else {
                handleReplyMessage();
            }
        } else if(event.getSource() == btnBack){
            handleBack();
        }
    }

    private void handleSendMessage(Utilizator friendSelected) {
        String message = messageField.getText();
        if (message.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Message cannot be empty!");
        } else {
            socialNetworkService.sendMessage(currentUser, friendSelected, message, -1L);
            modelMessages.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            messageField.clear();
            messageField.setPromptText("Enter your message...");
        }
    }

    private Message getSelectedMessage() {
        Message selected = chatListView.getSelectionModel().getSelectedItem();
        return selected;
    }


    private void handleReplyMessage() {
        Message selectedMessage = getSelectedMessage();
        String message = messageField.getText();
        if (message.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Message cannot be empty!");
        } else {
            socialNetworkService.sendMessage(currentUser, selectedMessage.getFrom(), message, selectedMessage.getId());
            //sort the messages by date
            modelMessages.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            messageField.setPromptText("Enter your message...");
        }
    }


    private void handleBack() {
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/mainpage.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Social Network");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MainPageController mainPageController = fxmlLoader.getController();
            mainPageController.setMainPage(socialNetworkService, dialogStage, currentUser);

            stage.close();
            dialogStage.show();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModelMessages(currentUser, friendSelected);
    }
}
