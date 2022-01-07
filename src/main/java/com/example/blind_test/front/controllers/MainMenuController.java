package com.example.blind_test.front.controllers;

import com.example.blind_test.front.models.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

public class MainMenuController extends Controller{

    private final ObservableList<Integer> numberOfPlayersList = FXCollections.observableArrayList(1, 2, 3, 4, 5);
    private final ObservableList<Integer> numberOfQuestionsList = FXCollections.observableArrayList(5, 10, 15, 20);
    private final ObservableList<Integer> responseTimeList = FXCollections.observableArrayList(10, 15, 20);

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
    void onCreateAudioGame(ActionEvent event) {
        //this.clientImpl.createGame();
    }

    @FXML
    void onCreateImageGame(ActionEvent event) {

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
