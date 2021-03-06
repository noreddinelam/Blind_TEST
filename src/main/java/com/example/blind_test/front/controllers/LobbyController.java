package com.example.blind_test.front.controllers;

import com.example.blind_test.HelloApplication;
import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.shared.communication.JoinGameType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.IOException;

public class LobbyController extends Controller {

    private int nbPlayersInGame = 0;
    private boolean isAdmin = false;

    @FXML
    private Text gameType;

    @FXML
    private ListView<Player> joinedPlayerList;

    @FXML
    private Text numberOfJoinedPlayers;

    @FXML
    private Text responseTime;

    @FXML
    private Text rounds;

    @FXML
    private Button startGame;

    @FXML
    private Button quitGame;

    @FXML
    void onStartGame(ActionEvent event) {
        if (isAdmin)
            this.clientImpl.startGame();
    }

    @FXML
    void onQuitGame(ActionEvent event) {
        if (this.isAdmin) this.clientImpl.deleteGame(this.clientImpl.getPlayer().getGame().getId());
        else {
            this.clientImpl.leaveGame(this.clientImpl.getPlayer().getGame().getId(),
                    this.clientImpl.getPlayer().getUsername());
        }
    }

    @Override
    public void onCloseFrame() {
        this.stage.setOnCloseRequest(event -> {
            this.onQuitGame(null);
            this.clientImpl.stopClient();
        });
    }

    @FXML
    private void initialize() {
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.clientImpl.setController(this);
        this.joinedPlayerList.setCellFactory((param) -> new ListCell<>() {
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
        startGame.setDisable(true);
    }

    public void initView(JoinGameType jgt) {
        Platform.runLater(() -> {
            if (jgt.getOtherPlayers() != null) {
                this.joinedPlayerList.getItems().setAll(jgt.getOtherPlayers());
                this.nbPlayersInGame = jgt.getOtherPlayers().size() + 1;
                this.isAdmin = false;
            } else {
                this.nbPlayersInGame = 1;
                this.isAdmin = true;
            }
            this.numberOfJoinedPlayers.setText(this.nbPlayersInGame + " / " + jgt.getPlayer().getGame().getTotalPlayers());
            this.joinedPlayerList.getItems().add(jgt.getPlayer());
            this.rounds.setText(String.valueOf(jgt.getPlayer().getGame().getRounds()));
            this.responseTime.setText(String.valueOf(jgt.getPlayer().getGame().getTimeQuestion()));
            this.gameType.setText(jgt.getPlayer().getGame().isImageGame() ? "Image Game" : "Audio Game");
            if (this.nbPlayersInGame == jgt.getPlayer().getGame().getTotalPlayers() && isAdmin) startGame.setDisable(false);

        });
    }

    public void addPlayerToListOfPlayers(Player player) {
        Platform.runLater(() -> {
            this.joinedPlayerList.getItems().add(player);
            this.numberOfJoinedPlayers.setText((++this.nbPlayersInGame) + " / " + player.getGame().getTotalPlayers());
            if (this.nbPlayersInGame == player.getGame().getTotalPlayers() && isAdmin) startGame.setDisable(false);
        });
    }

    public void removePlayerToListOfPlayers(String username, int totalPlayers) {
        Platform.runLater(() -> {
            Player player = new Player(username);
            this.joinedPlayerList.getItems().remove(player);
            this.numberOfJoinedPlayers.setText((--this.nbPlayersInGame) + " / " + totalPlayers);
            startGame.setDisable(true);
        });
    }

    public void startGame(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Vue.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.scene = this.scene;
            controller.stage = this.stage;
            controller.setTimePerQuestion(Integer.parseInt(this.responseTime.getText()));
            controller.setAdminGame(isAdmin);
            controller.setNbQuestions(Integer.parseInt(this.rounds.getText()));
            controller.initView(this.joinedPlayerList.getItems(), question, 1);
            this.scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
