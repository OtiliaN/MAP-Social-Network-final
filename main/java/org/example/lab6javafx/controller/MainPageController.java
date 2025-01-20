package org.example.lab6javafx.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.lab6javafx.Service.SocialNetwork;
import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.utils.events.ChangeEventType;
import org.example.lab6javafx.utils.events.EntityChangeEvent;
import org.example.lab6javafx.utils.observer.Observer;
import org.example.lab6javafx.utils.paging.Page;
import org.example.lab6javafx.utils.paging.Pageable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainPageController implements Observer<EntityChangeEvent> {
    private SocialNetwork socialNetworkService;
    private Stage stage;
    private Utilizator currentUser;

    private int current_page = 0;
    private static final int page_size = 4;

    //StackPane principal
    @FXML
    private TableView<Utilizator> friendsTable;

    @FXML
    private TableColumn<Utilizator, String> firstNameColumn;

    @FXML
    private TableColumn<Utilizator, String> lastNameColumn;

    @FXML
    private TableColumn<Utilizator, String> usernameColumn;

    @FXML
    private Button addFriendButton;

    @FXML
    private Button deleteFriendButton;

    @FXML
    private Button friendRequestsButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button messageButton;

    @FXML
    private Label pagesLabel;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Label notificationLabel;

    @FXML
    public ObservableList<Utilizator> friendsList = FXCollections.observableArrayList();

    public void setMainPage(SocialNetwork socialNetworkService, Stage stage, Utilizator currentUser) {
        this.socialNetworkService = socialNetworkService;
        this.stage = stage;
        this.currentUser = currentUser;
        socialNetworkService.addObserver(this);
       // lblGreeting.setText(currentUser.getUsername() + "'s account");
        initUsersFriends(currentUser);
    }

    @FXML
    public void initialize() {
        initializeTable();
    }

    private void initializeTable() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        friendsTable.setItems(friendsList);
        friendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void initUsersFriends(Utilizator user) {

        Page<Utilizator> page = socialNetworkService.findAllOnPage(new Pageable(current_page, page_size), user.getId());

        List<Utilizator> getfriendsList = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        friendsList.setAll(getfriendsList);
        int numberOfPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / page_size);
        pagesLabel.setText("Page " + (current_page + 1) + " of " + numberOfPage);

        previousButton.setDisable(current_page == 0);
        nextButton.setDisable(current_page == numberOfPage - 1);

    }

    @FXML
    private void handlePanelClicks(ActionEvent event) {
        if (event.getSource() == addFriendButton){
            handleAddFriend();
        } else if (event.getSource() == deleteFriendButton){
            handleDeleteFriend();
        } else if(event.getSource() == friendRequestsButton){
            handleShowFriendsRequests();
        } else if(event.getSource() == messageButton){
            handleMessages();
        } else
            if(event.getSource() == logOutButton){
            handleLogOut();
        }
    }

    private void handleMessages(){
        Utilizator selectedFriend = getSelectedFriend();
        if (selectedFriend == null) {
            MessageAlert.showMessage("Input Error", Alert.AlertType.WARNING, "WARNING", "No friend selected!");
            return;
        }
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/messages.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MessageController messageController = fxmlLoader.getController();
            messageController.setMessage(socialNetworkService, dialogStage, currentUser, selectedFriend);

            stage.close();
            dialogStage.show();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    private void handleLogOut() {
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/login.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("SocialNetwork");
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

    private void handleShowFriendsRequests() {
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/showfriendsrequests.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friends Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendsRequestsController friendsRequestsController = fxmlLoader.getController();
            friendsRequestsController.setFriendsRequests(socialNetworkService, dialogStage, currentUser);


            dialogStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    private Utilizator getSelectedFriend() {
        return friendsTable.getSelectionModel().getSelectedItem();
    }

    private void handleAddFriend() {
        try{
            URL resource = getClass().getResource("/org/example/lab6javafx/addfriend.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.centerOnScreen();

            System.out.println(currentUser.getUsername());
            AddFriendController addFriendController = fxmlLoader.getController();
            addFriendController.setAddFriend(socialNetworkService, dialogStage, currentUser);

            dialogStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void handleDeleteFriend() {
        Utilizator selectedFriend = getSelectedFriend();
        if (selectedFriend == null) {
            MessageAlert.showMessage("Input Error", Alert.AlertType.WARNING, "WARNING", "No friend selected!");
            return;
        }
        socialNetworkService.removeFriendship(currentUser.getId(), selectedFriend.getId());
        MessageAlert.showMessage("Success", Alert.AlertType.CONFIRMATION, "INFORMATION","Friend deleted successfully!");
    }


    public void onNextPage(ActionEvent actionEvent){
        current_page++;
        initUsersFriends(currentUser);
    }

    public void onBeforePage(ActionEvent actionEvent){
        current_page--;
        initUsersFriends(currentUser);
    }

    private void showFriendRequestNotification(Utilizator sender) {

        notificationLabel.setText("New friend request from " + sender.getUsername());
        notificationLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> notificationLabel.setVisible(false));
        pause.play();
    }

    @Override
    public void update(EntityChangeEvent event) {
        if (event.getType() == ChangeEventType.ADD && event.getData() instanceof Friendship) {
            Friendship friendship = (Friendship) event.getData();

            if (friendship.getUser2().getId().equals(currentUser.getId())) {

                Platform.runLater(() -> showFriendRequestNotification(friendship.getUser1()));
            }
        }
        initUsersFriends(currentUser);
    }
}
