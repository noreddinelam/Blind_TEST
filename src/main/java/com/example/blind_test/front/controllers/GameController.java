package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
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

    private Question currentQuestionModel;

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

                    try {
                        HBox hbox = new HBox(30);
                        String path = "src/main/resources/com/example/blind_test/images/player.png";
                        String path2 = "src/main/resources/com/example/blind_test/images/score.png";
                        FileInputStream inputPlayer = new FileInputStream(path);
                        FileInputStream inputScore = new FileInputStream(path2);
                        Image image1 = new Image(inputPlayer, 30, 30, true, true);
                        Image image2 = new Image(inputScore, 30, 30, true, true);
                        ImageView playerView = new ImageView(image1);
                        ImageView scoreView = new ImageView(image2);
                        List<Node> itemsInHbox = new ArrayList<>();
                        itemsInHbox.add(playerView);
                        itemsInHbox.add(new Text(player.getUsername()));
                        itemsInHbox.add(scoreView);
                        itemsInHbox.add(new Text(""+player.getScore()));
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
            this.currentPlayerName.setText(this.clientImpl.getPlayer().getUsername());
        });
    }

}
