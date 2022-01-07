package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    //TODO : make test of textField empty.
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
                        if(game.isImageGame())
                            path = "src/main/resources/com/example/blind_test/images/image.png";
                        else
                            path = "src/main/resources/com/example/blind_test/images/audio.png";
                        FileInputStream input = new FileInputStream(path);
                        Image image = new Image(input,30,30,true,true);
                        ImageView imageView = new ImageView(image);
                        List<Node> itemsInVbox = new ArrayList<>();
                        itemsInVbox.add(new Text("Questions  : " + game.getRounds()));
                        itemsInVbox.add(new Text("Players : " + game.getPlayers()));
                        itemsInVbox.add(new Text("Time per question : " + game.getTimeQuestion()));
                        vbox1.getChildren().setAll(itemsInVbox);
                        hbox.getChildren().addAll(vbox1,imageView);
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


}
