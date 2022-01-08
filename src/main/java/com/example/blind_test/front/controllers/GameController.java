package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.front.models.Timer;
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

    @FXML
    private Text currentPlayerName;

    private Question currentQuestionModel;
    private int timePerQuestion;

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
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseA.getText());
    }

    @FXML
    void onResponseB(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseB.getText());
    }

    @FXML
    void onResponseC(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseC.getText());
    }

    @FXML
    void onResponseD(ActionEvent event) {
        this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseD.getText());
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

    public void initView(List<Player> list, Question firstQuestion,int timePerQuestion){
        Platform.runLater(()->{
            this.scoreBoard.getItems().setAll(list);
            this.currentQuestionModel = firstQuestion;
            this.responseA.setText(firstQuestion.getChoiceByIndex(0));
            this.responseB.setText(firstQuestion.getChoiceByIndex(1));
            this.responseC.setText(firstQuestion.getChoiceByIndex(2));
            this.responseD.setText(firstQuestion.getChoiceByIndex(3));
            this.timePerQuestion = timePerQuestion;
            this.timer.setText(String.valueOf(timePerQuestion));
            new Timer(timePerQuestion,this).start();
            this.currentPlayerName.setText(this.clientImpl.getPlayer().getUsername());
        });
    }

    public void setTimerTime(int remainingTime){
        this.timer.setText(String.valueOf(remainingTime));
    }

}
