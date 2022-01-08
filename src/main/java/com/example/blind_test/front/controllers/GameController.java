package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.List;

public class GameController extends Controller {

    private Question currentQuestionModel;
    private Button clickedButton;
    private boolean responded = false;

    @FXML
    private Button quitGame;

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
        this.clickedButton = responseA;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseA.getText());
        setResponded();
    }

    @FXML
    void onResponseB(ActionEvent event) {
        this.clickedButton = responseB;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseB.getText());
        setResponded();
    }

    @FXML
    void onResponseC(ActionEvent event) {
        this.clickedButton = responseC;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseC.getText());
        setResponded();
    }

    @FXML
    void onResponseD(ActionEvent event) {
        this.clickedButton = responseD;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseD.getText());
        setResponded();
    }

    @FXML
    void onQuitGame(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.clientImpl.setController(this);
        this.scoreBoard.setCellFactory((param) -> new ListCell<>() {
            @Override
            protected void updateItem(Player player, boolean b) {
                super.updateItem(player, b);
                if (b || player == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(player.getUsername());
                }
            }
        });
    }

    public void initListPlayer(List<Player> list) {
        Platform.runLater(() -> {
            this.scoreBoard.getItems().addAll(list);
        });
    }

    public void initView(List<Player> list, Question firstQuestion) {
        Platform.runLater(() -> {
            this.scoreBoard.getItems().setAll(list);
            this.currentQuestionModel = firstQuestion;
            this.responseA.setText(firstQuestion.getChoiceByIndex(0));
            this.responseB.setText(firstQuestion.getChoiceByIndex(1));
            this.responseC.setText(firstQuestion.getChoiceByIndex(2));
            this.responseD.setText(firstQuestion.getChoiceByIndex(3));
        });
    }

    public void changeQuestionState(String color) {
        Platform.runLater(()->{
            this.clickedButton.setStyle(color);
        });
    }

    public void setResponded(){
        responded = true;
    }

    public void updateScoreBoard(Player p){
        int index = this.scoreBoard.getItems().indexOf(p);
        Platform.runLater(() -> {
           this.scoreBoard.getItems().set(index,p);
        });

    }

}
