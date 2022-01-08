package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

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
    private Text winner;

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
    }

    public void initView(List<Player> players, int numberOfQuestions, int responseTime) {
    }

}
