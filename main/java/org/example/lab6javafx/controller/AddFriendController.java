package org.example.lab6javafx.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.lab6javafx.Service.SocialNetwork;
import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.FriendshipStatus;
import org.example.lab6javafx.domain.Utilizator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddFriendController {
    private SocialNetwork socialNetworkService;
    private Stage dialogStage;
    private Utilizator user;

    @FXML
    private TableView<Utilizator> usersTable;

    @FXML
    private TableColumn<Utilizator, String> tblFirstName;

    @FXML
    private TableColumn<Utilizator, String> tblLastName;

    @FXML
    private TableColumn<Utilizator, String> tblUsername;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnAddFriend;

    private ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();

    public void setAddFriend(SocialNetwork socialNetworkService, Stage dialogStage, Utilizator user) {
        this.socialNetworkService = socialNetworkService;
        this.dialogStage = dialogStage;
        this.user = user;
        initModelUsers();
        initializeUserTable();
    }

    @FXML
    public void initialize() {
        initializeUserTable();
    }

    private void initializeUserTable() {
        tblFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tblLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tblUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        usersTable.setItems(modelUsers);
    }

    private void initModelUsers() {
        List<Utilizator> users=socialNetworkService.getUserNotFriendsWith(user);
        List<Utilizator> usersList= StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        modelUsers.setAll(usersList);
    }

    private Utilizator getSelectedFriend() {
        return usersTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleAddFriend() {
        Utilizator selectedUser = getSelectedFriend();
        if (selectedUser == null) {
            MessageAlert.showMessage("Input Error", Alert.AlertType.ERROR, "ERROR", "No user selected.");
            return;
        }
        if(user.getId() == selectedUser.getId()) {
            MessageAlert.showMessage("Input Error", Alert.AlertType.ERROR, "ERROR", "Same user selected.");
            return;
        }
        Friendship friendship = socialNetworkService.findFriendship(user.getId(), selectedUser.getId());
        if(friendship == null) {
            socialNetworkService.addFriendship(user.getId(), selectedUser.getId());
            MessageAlert.showMessage("Success", Alert.AlertType.INFORMATION, "Successfully", "Friend Request Sent!");
            return;
        }
        if(friendship.getStatus().equals(FriendshipStatus.ACCEPTED.name())){
            MessageAlert.showMessage("Unsuccessful", Alert.AlertType.INFORMATION, "Unsuccessfully", "Friendship already exists!");
            return;
        } else{
            MessageAlert.showMessage("Wait", Alert.AlertType.INFORMATION, "Waiting", "Friend still hasn't accepted this request!");
            return;
        }
    }

    @FXML
    private void handleCancel() {
        this.dialogStage.close();
    }
}
