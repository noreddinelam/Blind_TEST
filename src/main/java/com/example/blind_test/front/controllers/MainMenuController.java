package com.example.blind_test.front.controllers;

import com.example.blind_test.HelloApplication;
import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.other.FailureMessages;
import com.example.blind_test.shared.communication.JoinGameType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private Button quitGame;

    @FXML
    void onCreateAudioGame(ActionEvent event) {
        if (!this.usernameText.getText().trim().isEmpty())
            this.clientImpl.createGame(false, false, numberOfQuestions.getValue(), numberOfPlayers.getValue(),
                    responseTime.getValue(),
                    usernameText.getText().trim());
        else {
            this.usernameText.setText("");
            this.commandFailed(FailureMessages.USERNAME_EMPTY_TITLE, FailureMessages.USERNAME_EMPTY_MESSAGE);
        }
    }

    @FXML
    void onCreateImageGame(ActionEvent event) {
        if (!this.usernameText.getText().trim().isEmpty())
            this.clientImpl.createGame(true, false, numberOfQuestions.getValue(), numberOfPlayers.getValue(),
                    responseTime.getValue(),
                    usernameText.getText().trim());
        else {
            this.usernameText.setText("");
            this.commandFailed(FailureMessages.USERNAME_EMPTY_TITLE, FailureMessages.USERNAME_EMPTY_MESSAGE);
        }
    }

    @FXML
    void onJoinGame(ActionEvent event) {
        if (!this.usernameText.getText().trim().isEmpty())
            this.clientImpl.joinGame(listOfGameToJoin.getSelectionModel().getSelectedItem().getId(),
                    this.usernameText.getText().trim());
        else {
            this.usernameText.setText("");
            this.commandFailed(FailureMessages.USERNAME_EMPTY_TITLE, FailureMessages.USERNAME_EMPTY_MESSAGE);
        }
    }

    @FXML
    void onQuitGame(ActionEvent event) {
        //TODO : delete game when closing
        Stage stage = (Stage) quitGame.getScene().getWindow();
        this.clientImpl.setAdmin(false);
        stage.close();
    }

    @FXML
    private void initialize() {
        numberOfPlayers.setItems(numberOfPlayersList);
        numberOfQuestions.setItems(numberOfQuestionsList);
        responseTime.setItems(responseTimeList);
        numberOfPlayers.getSelectionModel().select(2);
        numberOfQuestions.getSelectionModel().selectFirst();
        responseTime.getSelectionModel().selectFirst();
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.listOfGameToJoin.setCellFactory((param) -> new ListCell<>() {
            @Override
            protected void updateItem(Game game, boolean b) {
                super.updateItem(game, b);
                if (b || game == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(30);
                    VBox vbox1 = new VBox();
                    try {
                        String path;
                        if (game.isImageGame())
                            path = "src/main/resources/com/example/blind_test/images/image.png";
                        else
                            path = "src/main/resources/com/example/blind_test/images/audio.png";
                        FileInputStream input = new FileInputStream(path);
                        Image image = new Image(input, 30, 30, true, true);
                        ImageView imageView = new ImageView(image);
                        List<Node> itemsInVbox = new ArrayList<>();
                        itemsInVbox.add(new Text("Questions  : " + game.getRounds()));
                        itemsInVbox.add(new Text("Players : " + game.getTotalPlayers()));
                        itemsInVbox.add(new Text("Time per question : " + game.getTimeQuestion()));
                        vbox1.getChildren().setAll(itemsInVbox);
                        hbox.getChildren().addAll(vbox1, imageView);
                        hbox.setAlignment(Pos.CENTER);
                        setGraphic(hbox);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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

    public void enterGameSucceeded(JoinGameType jgt) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Lobby.fxml"));
            Parent root = loader.load();
            LobbyController controller = loader.getController();
            controller.scene = this.scene;
            controller.initView(jgt);
            this.scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addGameToListGameToJoin(Game game) {
        Platform.runLater(() -> {
            this.listOfGameToJoin.getItems().add(game);
        });
    }


}
