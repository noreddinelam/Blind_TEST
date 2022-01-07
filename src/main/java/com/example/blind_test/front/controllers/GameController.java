package com.example.blind_test.front.controllers;

import com.example.blind_test.front.models.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.List;

public class GameController extends Controller {


    @FXML
    private Button responseA;

    @FXML
    private Button responseB;

    @FXML
    private Button responseC;

    @FXML
    private Button responseD;

    @FXML
    private ImageView currentQuestion;

    @FXML
    private Text round;

    @FXML
    private ListView<Player> scoreBoard;

    @FXML
    private Text timer;

    @FXML
    void onResponseA(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()),responseA.getText());
    }

    @FXML
    void onResponseB(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()),responseB.getText());
    }

    @FXML
    void onResponseC(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()),responseC.getText());
    }

    @FXML
    void onResponseD(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()),responseD.getText());
    }

    @FXML
    private void initialize() {
        this.clientImpl.setController(this);
        this.clientImpl.nextRound(Integer.parseInt(this.round.getText()));
    }

    public void initListPlayer(List<Player> list) {
        Platform.runLater(() -> {
            this.scoreBoard.getItems().addAll(list);
        });
    }

}
