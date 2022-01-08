package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.shared.communication.JoinGameType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class LobbyController extends Controller {

    @FXML
    private Text gameType;

    @FXML
    private ListView<Player> joinedPlayerList;

    @FXML
    private Text numberOfJoinedPlayers;

    @FXML
    private Text remainingTime;

    @FXML
    private Text responseTime;

    @FXML
    private Text rounds;

    @FXML
    private Button startGame;

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
    }

    public void initView(JoinGameType jgt) {
        Platform.runLater(() -> {
            if (jgt.getOtherPlayers() != null) {
                this.joinedPlayerList.getItems().setAll(jgt.getOtherPlayers());
                this.numberOfJoinedPlayers.setText("1 / " + jgt.getPlayer().getGame().getPlayers());
            } else
                this.numberOfJoinedPlayers.setText((jgt.getOtherPlayers().size() + 1) + " / " + jgt.getPlayer().getGame().getPlayers());
            this.joinedPlayerList.getItems().add(jgt.getPlayer());
            this.rounds.setText(String.valueOf(jgt.getPlayer().getGame().getRounds()));
            this.responseTime.setText(String.valueOf(jgt.getPlayer().getGame().getTimeQuestion()));
            this.gameType.setText(jgt.getPlayer().getGame().isImageGame() ? "Image Game" : "Audio Game");
        });
    }

}
