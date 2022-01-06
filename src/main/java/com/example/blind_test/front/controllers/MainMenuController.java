package com.example.blind_test.front.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class MainMenuController {

    ObservableList<String> numberOfPlayersList = FXCollections.observableArrayList("2","3","4","5","6","7","8");
    ObservableList<String> numberOfQuestionsList = FXCollections.observableArrayList("10","20","30","40");
    ObservableList<String> responseTimeList = FXCollections.observableArrayList("5","10","15");


    @FXML
    private ChoiceBox<String> numberOfPlayers;

    @FXML
    private ChoiceBox<String> numberOfQuestions;

    @FXML
    private ChoiceBox<String> responseTime;

    @FXML
    private void initialize(){
        numberOfPlayers.setItems(numberOfPlayersList);
        numberOfQuestions.setItems(numberOfQuestionsList);
        responseTime.setItems(responseTimeList);
    }


}
