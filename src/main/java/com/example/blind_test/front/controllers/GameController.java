package com.example.blind_test.front.controllers;

import com.example.blind_test.HelloApplication;
import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.front.models.Timer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class GameController extends Controller {

    @FXML
    private Text currentPlayerName;

    private Button clickedButton;
    private boolean responded = false;
    private int timePerQuestion;
    private boolean adminGame = false;
    private int nbQuestions;
    private Clip clip;
    private Timer timerThread;

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
        if (!this.responded){
            this.clickedButton = responseA;
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseA.getText());
        }
    }

    @FXML
    void onResponseB(ActionEvent event) {
        if (!this.responded) {
            this.clickedButton = responseB;
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseB.getText());
        }
    }

    @FXML
    void onResponseC(ActionEvent event) {
        if (!this.responded) {
            this.clickedButton = responseC;
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseC.getText());
        }
    }

    @FXML
    void onResponseD(ActionEvent event) {
        if (!this.responded) {
            this.clickedButton = responseD;
            this.clientImpl.getQuestionResponse(Integer.parseInt(round.getText()), responseD.getText());
        }
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
            changeColorResponseButtons("-fx-background-color: #343a40");
            this.responded = false;
            this.round.setText(String.valueOf(questionOrder));
            this.timer.setText(String.valueOf(this.timePerQuestion));
            this.timerThread =  new Timer(this.timePerQuestion, this);
            this.timerThread.start();
            try {
                String path = question.getResource();
                if(!this.clientImpl.getPlayer().getGame().isImageGame()) {
                    clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path));
                    clip.open(inputStream);
                    clip.start();
                    path = "src/main/resources/com/example/blind_test/images/music.jpg";
                }
                FileInputStream questionImage = new FileInputStream(path);
                Image image1 = new Image(questionImage, 400, 418, false, true);
                currentQuestion.setImage(image1);
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
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
        if(!this.clientImpl.getPlayer().getGame().isImageGame())
            clip.stop();
        if (this.adminGame) {
            int round = Integer.parseInt(this.round.getText());
            if (round < this.nbQuestions)
                this.clientImpl.nextRound(round + 1);
            else {
                this.clientImpl.gameFinished();
            }
        }
    }

    public void changeColorResponseButtons(String color){
        Platform.runLater(() ->{
            this.responseA.setStyle(color);
            this.responseB.setStyle(color);
            this.responseC.setStyle(color);
            this.responseD.setStyle(color);
        });
    }

    public void removePlayerToListOfPlayers(String username){
        Platform.runLater(() -> {
            Player player = new Player(username);
            this.scoreBoard.getItems().remove(player);
        });
    }

    public void gameFinished(){
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Scoreboard.fxml"));
            Parent root = loader.load();
            ScoreBoardController controller = loader.getController();
            controller.scene = this.scene;
            controller.stage = this.stage;
            controller.onCloseFrame();
            controller.initView(scoreBoard.getItems(),this.nbQuestions,this.timePerQuestion);
            this.scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopWorking(){
        if(!this.clientImpl.getPlayer().getGame().isImageGame())
            this.clip.stop();
        this.timerThread.setInterrupt(true);
    }
}
