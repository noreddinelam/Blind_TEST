package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.front.models.Timer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GameController extends Controller {

    @FXML
    private Text currentPlayerName;

    private Button clickedButton;
    private boolean responded = false;
    private int timePerQuestion;
    private boolean adminGame = false;
    private int nbQuestions;

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
    }

    @FXML
    void onResponseB(ActionEvent event) {
        this.clickedButton = responseB;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseB.getText());
    }

    @FXML
    void onResponseC(ActionEvent event) {
        this.clickedButton = responseC;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseC.getText());
    }

    @FXML
    void onResponseD(ActionEvent event) {
        this.clickedButton = responseD;
        if (!this.responded)
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseD.getText());
    }

    @FXML
    void onQuitGame(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.clientImpl.setController(this);
        this.currentPlayerName.setText(this.clientImpl.getPlayer().getUsername());
        this.scoreBoard.setCellFactory((param) -> new ListCell<>() {
            @Override
            protected void updateItem(Player player, boolean b) {
                super.updateItem(player, b);
                if (b || player == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        HBox hbox = new HBox(30);
                        String path = "src/main/resources/com/example/blind_test/images/player.png";
                        String path2 = "src/main/resources/com/example/blind_test/images/score.png";
                        FileInputStream inputPlayer = new FileInputStream(path);
                        FileInputStream inputScore = new FileInputStream(path2);
                        Image image1 = new Image(inputPlayer, 30, 30, true, false);
                        Image image2 = new Image(inputScore, 30, 30, false, false);
                        ImageView playerView = new ImageView(image1);
                        ImageView scoreView = new ImageView(image2);
                        List<Node> itemsInHbox = new ArrayList<>();
                        itemsInHbox.add(playerView);
                        itemsInHbox.add(new Text(player.getUsername()));
                        itemsInHbox.add(scoreView);
                        itemsInHbox.add(new Text("" + player.getScore()));
                        hbox.getChildren().setAll(itemsInHbox);
                        hbox.setAlignment(Pos.CENTER);
                        setGraphic(hbox);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void initView(List<Player> list, Question question, int questionOrder) {
        Platform.runLater(() -> {
            this.scoreBoard.getItems().setAll(list);
            this.responseA.setText(question.getChoiceByIndex(0));
            this.responseB.setText(question.getChoiceByIndex(1));
            this.responseC.setText(question.getChoiceByIndex(2));
            this.responseD.setText(question.getChoiceByIndex(3));
            if (clickedButton != null)
                clickedButton.setStyle("-fx-background-color: #343a40");
            this.responded = false;
            this.round.setText(String.valueOf(questionOrder));
            this.timer.setText(String.valueOf(timePerQuestion));
            new Timer(timePerQuestion, this).start();
            this.currentPlayerName.setText(this.clientImpl.getPlayer().getUsername());
            try {
                String path = question.getResource();
                FileInputStream questionImage = new FileInputStream(path);
                Image image1 = new Image(questionImage, 400, 418, false, true);
                currentQuestion.setImage(image1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void setTimePerQuestion(int timePerQuestion) {
        this.timePerQuestion = timePerQuestion;
    }

    public void changeQuestionState(String color) {
        Platform.runLater(() -> {
            this.clickedButton.setStyle(color);
        });
    }

    public void setResponded() {
        responded = true;
    }


    public void setAdminGame(boolean adminGame) {
        this.adminGame = adminGame;
    }

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public void updateScoreBoard(Player p) {
        int index = this.scoreBoard.getItems().indexOf(p);
        Platform.runLater(() -> {
            this.scoreBoard.getItems().set(index, p);
        });
    }

    public void setTimerTime(int remainingTime) {
        this.timer.setText(String.valueOf(remainingTime));
    }

    public void nextRound() {
        if (this.adminGame) {
            int round = Integer.parseInt(this.round.getText());
            if (round < this.nbQuestions)
                this.clientImpl.nextRound(round + 1);
            else {
                //TODO : send request to delete the game and return to mainMenuScreen;
            }
        }
    }
}
