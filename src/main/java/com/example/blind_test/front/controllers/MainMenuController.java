package com.example.blind_test.front.controllers;

import com.example.blind_test.front.models.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainMenuController extends Controller {

    ObservableList<Integer> numberOfPlayersList = FXCollections.observableArrayList(1, 2, 3, 4, 5);
    ObservableList<Integer> numberOfQuestionsList = FXCollections.observableArrayList(5, 10, 15, 20);
    ObservableList<Integer> responseTimeList = FXCollections.observableArrayList(10, 15, 20);

    @FXML
    private Button createAudioGameButton;

    @FXML
    private Button createImageGameButton;

    @FXML
    private Button joinGameButton;

    @FXML
    private ListView<Game> listOfGameToJoin;

    @FXML
    private ChoiceBox<Integer> numberOfPlayers;

    @FXML
    private ChoiceBox<Integer> numberOfQuestions;

    @FXML
    private ChoiceBox<Integer> responseTime;

    @FXML
    private TextField usernameText;


    @FXML
    void onCreateAudioGame(ActionEvent event) {
        this.clientImpl.createGame(false, false, numberOfQuestions.getValue(), numberOfPlayers.getValue(),responseTime.getValue(),
                usernameText.getText());
    }

    @FXML
    void onCreateImageGame(ActionEvent event) {
        this.clientImpl.createGame(true, false, numberOfQuestions.getValue(), numberOfPlayers.getValue(),responseTime.getValue(),
                usernameText.getText());
    }

    @FXML
    void onJoinGame(ActionEvent event) {

    }

    @FXML
    void onQuitGame(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        numberOfPlayers.setItems(numberOfPlayersList);
        numberOfQuestions.setItems(numberOfQuestionsList);
        responseTime.setItems(responseTimeList);
    }


}
