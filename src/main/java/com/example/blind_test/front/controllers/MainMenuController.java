package com.example.blind_test.front.controllers;

import com.example.blind_test.HelloApplication;
import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.Icon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuController extends Controller {

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
    private TextField usernameText;


    @FXML
    void onCreateAudioGame(ActionEvent event) {
        this.clientImpl.createGame(false, false, numberOfQuestions.getValue(), numberOfPlayers.getValue(),
                responseTime.getValue(),
                usernameText.getText());
    }

    @FXML
    void onCreateImageGame(ActionEvent event) {
        this.clientImpl.createGame(true, false, numberOfQuestions.getValue(), numberOfPlayers.getValue(),
                responseTime.getValue(),
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
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.listOfGameToJoin.setCellFactory((param) -> new ListCell<>() {
            @Override
            protected void updateItem(Game game, boolean b) {
                super.updateItem(game, b);
                if (b || game == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    List<Node> itemsInVbox = new ArrayList<>();
                    itemsInVbox.add(new Text(String.valueOf(game.getRounds())));
                    itemsInVbox.add(new Text(String.valueOf(game.getPlayers())));
                    itemsInVbox.add(new Text(String.valueOf(game.getTimeQuestion())));
                    vbox.getChildren().setAll(itemsInVbox);
                    setGraphic(vbox);
                }
            }
        });
    }

    public void initializeListOfUnStartedGames() {
        this.clientImpl.listOfNotStartedGame();
    }

    public void setUnStartedGames(List<Game> list) {
        Platform.runLater(() -> {
            this.listOfGameToJoin.getItems().setAll(list);
        });
    }
    public void createGameSucceeded() {

        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Lobby.fxml"));
            Parent root = null;
            root = loader.load();
            LobbyController controller=loader.getController();
            controller.scene=this.scene;
            this.scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
