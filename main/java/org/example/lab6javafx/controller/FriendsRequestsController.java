package org.example.lab6javafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.lab6javafx.Service.SocialNetwork;
import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.FriendshipStatus;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.utils.events.ChangeEventType;
import org.example.lab6javafx.utils.events.EntityChangeEvent;
import org.example.lab6javafx.utils.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendsRequestsController implements Observer<EntityChangeEvent> {

    private SocialNetwork socialNetworkService;
    private Stage dialogStage;
    private Utilizator user;
    private ObservableList<Friendship> modelFriendRequests = FXCollections.observableArrayList();

    @FXML
    private TableView<Friendship> tableViewRequests;

    @FXML
    private TableColumn<Friendship, String> tblSender;

    @FXML
    private TableColumn<Friendship, String> tblStatus;

    @FXML
    private TableColumn<Friendship, String> tblDate;

    @FXML
    public Button btnGoBack;

    public void setFriendsRequests(SocialNetwork socialNetworkService, Stage dialogStage, Utilizator user) {
        this.socialNetworkService = socialNetworkService;
        this.dialogStage = dialogStage;
        this.user = user;
        this.socialNetworkService.addObserver(this);
        initializeModelFriendRequests();
        initModelFriendRequests();
    }

    private void initModelFriendRequests() {
        tblSender.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser1().getUsername()));
        tblDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.
                ofPattern("yyyy-MM-dd HH:mm:ss"))));
        tblStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        tableViewRequests.setItems(modelFriendRequests);
    }

    private void initializeModelFriendRequests(){
        List<Friendship> requests = socialNetworkService.getRequests(user.getUsername());
        modelFriendRequests.setAll(requests);
    }

    private Friendship getSelectedRequest() {
        return tableViewRequests.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleUpdateRequest() {
        Friendship friendrequest = getSelectedRequest();
        if (friendrequest == null) {
            MessageAlert.showMessage("Input Error", Alert.AlertType.ERROR, "ERROR", "No user selected.");
            return;
        }

        FriendshipStatus status = showStatusChangeDialog();
        boolean succes = socialNetworkService.modifyFriendship(friendrequest.getUser1().getId(), friendrequest.getUser2().getId(), status);
        if(!succes) {
            MessageAlert.showMessage("Input Error", Alert.AlertType.ERROR, "ERROR", "Something went wrong");
            return;
        }
    }

    private FriendshipStatus showStatusChangeDialog() {
        ChoiceDialog<FriendshipStatus> dialog = new ChoiceDialog<>(FriendshipStatus.ACCEPTED,
                FriendshipStatus.ACCEPTED, FriendshipStatus.REJECTED);
        dialog.setTitle("Modify Friendship Request");
        dialog.setHeaderText("Select the new status:");
        dialog.setContentText("Choose status:");

        return dialog.showAndWait().orElse(null);
    }

    @Override
    public void update(EntityChangeEvent event) {
        if (event.getType() == ChangeEventType.UPDATE || event.getType() == ChangeEventType.DELETE) {
            initializeModelFriendRequests();
            tableViewRequests.refresh();
        }
    }

    @FXML
    private void handleGoBack() {
        this.dialogStage.close();
    }
}
