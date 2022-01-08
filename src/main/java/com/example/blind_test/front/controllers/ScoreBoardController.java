package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreBoardController extends Controller {

    @FXML
    private ListView<Player> playersList;

    @FXML
    private Button quitGame;

    @FXML
    private Text responseTime;

    @FXML
    private Button returnToMainMenu;

    @FXML
    private Text rounds;

    @FXML
    void onQuitGame(ActionEvent event) {

    }

    @FXML
    void onReturnToMainMenu(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.clientImpl.setController(this);
        this.playersList.setCellFactory((param) -> new ListCell<>() {
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

    public void initView(List<Player> players, int numberOfQuestions, int responseTime) {
        Platform.runLater(() -> {

            Collections.sort(players, Comparator.comparingInt(Player::getScore));
            this.playersList.getItems().setAll(players);
            this.rounds.setText(String.valueOf(numberOfQuestions));
            this.responseTime.setText(String.valueOf(responseTime));
        });
    }
}
