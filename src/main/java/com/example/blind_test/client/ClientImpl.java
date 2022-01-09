package com.example.blind_test.client;


import com.example.blind_test.front.controllers.Controller;
import com.example.blind_test.front.controllers.GameController;
import com.example.blind_test.front.controllers.LobbyController;
import com.example.blind_test.front.controllers.MainMenuController;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.front.other.FailureMessages;
import com.example.blind_test.shared.CommunicationTypes;
import com.example.blind_test.shared.FieldsRequestName;
import com.example.blind_test.shared.NetCodes;
import com.example.blind_test.shared.Properties;
import com.example.blind_test.shared.communication.JoinGameType;
import com.example.blind_test.shared.communication.NextRoundInformation;
import com.example.blind_test.shared.communication.Request;
import com.example.blind_test.shared.communication.Response;
import com.example.blind_test.shared.gson_configuration.GsonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ClientImpl {
    protected static final Hashtable<String, Consumer<String>> listOfFunctions = new Hashtable<>();
    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private static final ClientImpl clientImpl = new ClientImpl();

    private String ipAddress;
    private AsynchronousSocketChannel client;
    private Controller controller;
    private Player player;

    private ClientImpl() {
    }

    public static ClientImpl getUniqueInstanceClientImpl() {
        return clientImpl;
    }

    public static Consumer<String> getFunctionWithRequestCode(Response response) {
        return listOfFunctions.get(response.getNetCode());
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void initThreadReader() {
        Thread reader = new Thread(() -> {
            ByteBuffer buffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            try {
                while (client.isOpen()) {
                    int nb = client.read(buffer).get();
                    String jsonRes = new String(buffer.array()).substring(0, nb);
                    logger.info("The received response \n{}", jsonRes);
                    Response response = GsonConfiguration.gson.fromJson(jsonRes, Response.class);
                    ClientImpl.getFunctionWithRequestCode(response).accept(response.getResponse());
                    buffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        reader.start();
    }

    public void initListOfFunctions() {
        listOfFunctions.put(NetCodes.CREATE_GAME_SUCCEED, this::createGameSucceeded);
        listOfFunctions.put(NetCodes.CREATE_GAME_BROADCAST_SUCCEED, this::createGameBroadcastSucceeded);
        listOfFunctions.put(NetCodes.CREATE_GAME_BROADCAST_FAILED, this::createGameBroadcastFailed);
        listOfFunctions.put(NetCodes.DELETE_GAME_BROADCAST_SUCCEED, this::deleteGameBroadcastSucceeded);
        listOfFunctions.put(NetCodes.JOIN_GAME_SUCCEED, this::joinGameSucceeded);
        listOfFunctions.put(NetCodes.JOIN_GAME_BROADCAST_SUCCEED, this::joinGameBroadcastSucceeded);
        listOfFunctions.put(NetCodes.JOIN_GAME_BROADCAST_FAILED, this::joinGameBroadcastFailed);
        listOfFunctions.put(NetCodes.LIST_OF_GAME_NOT_STARTED_SUCCEED, this::listOfNotStartedGameSucceeded);
        listOfFunctions.put(NetCodes.MODIFY_SCORE_SUCCEED, this::modifyPlayerScoreSucceeded);
        listOfFunctions.put(NetCodes.GET_RESPONSE_FOR_QUESTION_SUCCEED, this::getQuestionResponseSucceeded);
        listOfFunctions.put(NetCodes.START_GAME_SUCCEED, this::startGameSucceeded);
        listOfFunctions.put(NetCodes.START_GAME_FAILED, this::startGameFailed);
        listOfFunctions.put(NetCodes.GET_RESPONSE_FOR_QUESTION_FAILED, this::getQuestionResponseFailed);
        listOfFunctions.put(NetCodes.MODIFY_SCORE_FAILED, this::modifyPlayerScoreFailed);
        listOfFunctions.put(NetCodes.LIST_OF_GAME_NOT_STARTED_FAILED, this::listOfNotStartedGameFailed);
        listOfFunctions.put(NetCodes.CREATE_GAME_FAILED, this::createGameFailed);
        listOfFunctions.put(NetCodes.JOIN_GAME_FAILED, this::joinGameFailed);
        listOfFunctions.put(NetCodes.DELETE_GAME_FAILED, this::deleteGameFailed);
        listOfFunctions.put(NetCodes.NEXT_ROUND_SUCCEEDED, this::nextRoundSucceeded);
        listOfFunctions.put(NetCodes.NEXT_ROUND_FAILED, this::nextRoundInformationFailed);
        listOfFunctions.put(NetCodes.GAME_FINISHED_SUCCEED, this::gameFinishedSucceeded);
        listOfFunctions.put(NetCodes.GAME_FINISHED_FAILED, this::gameFinishedFailed);
        listOfFunctions.put(NetCodes.LEAVE_GAME_FAILED, this::leaveGameFailed);
        listOfFunctions.put(NetCodes.LEAVE_GAME_SUCCEED, this::leaveGameSucceed);
        listOfFunctions.put(NetCodes.LEAVE_GAME_BROADCAST, this::leaveGameBroadcast);
        listOfFunctions.put(NetCodes.REMOVE_GAME_FROM_LIST_OF_AVAILABLE_GAMES,
                this::removeGameFromListOfAvailableGames);
    }

    private void leaveGameSucceed(String s) {
        this.controller.backMainMenu();
    }

    private void leaveGameFailed(String s) {
        this.controller.commandFailed("Leave Game ERROR", "Sorry, You can't leave this game");
    }

    private void leaveGameBroadcast(String usernameOfLeftPlayer) {
        ((LobbyController) this.controller).removePlayerToListOfPlayers(usernameOfLeftPlayer,
                this.player.getGame().getTotalPlayers());
    }

    //TODO: There are two types of Broadcast : type one for joinedPlayers and type two for MainMenuPlayers
    private void deleteGameBroadcastSucceeded(String s) {
        this.controller.backMainMenu();
    }

    public void createGameSucceeded(String responseData) {
        Player player = GsonConfiguration.gson.fromJson(responseData, Player.class);
        this.player = player;
        ((MainMenuController) this.controller).enterGameSucceeded(new JoinGameType(player));
    }

    public void createGameBroadcastSucceeded(String responseData) {
        Game game = GsonConfiguration.gson.fromJson(responseData, Game.class);
        if (this.controller instanceof MainMenuController)
            ((MainMenuController) this.controller).addGameToListGameToJoin(game);
    }

    public void createGameBroadcastFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.CREATE_GAME_BROADCAST, responseData);
    }

    private void joinGameSucceeded(String responseData) {
        JoinGameType joinGameType = GsonConfiguration.gson.fromJson(responseData, JoinGameType.class);
        this.player = joinGameType.getPlayer();
        ((MainMenuController) this.controller).enterGameSucceeded(joinGameType);
    }

    private void joinGameBroadcastSucceeded(String responseData) {
        Player player = GsonConfiguration.gson.fromJson(responseData, Player.class);
        ((LobbyController) this.controller).addPlayerToListOfPlayers(player);
    }

    private void joinGameBroadcastFailed(String s) {
    }

    public void listOfNotStartedGameSucceeded(String responseData) {
        Map<String, List<Game>> games = GsonConfiguration.gson.fromJson(responseData,
                CommunicationTypes.mapListGameJsonTypeData);
        ((MainMenuController) this.controller).setUnStartedGames(games.get(FieldsRequestName.LIST_GAMES));
    }

    public void startGameSucceeded(String responseData) {
        Question question = GsonConfiguration.gson.fromJson(responseData, Question.class);
        ((LobbyController) this.controller).startGame(question);
        this.player.getGame().setState(true);
    }

    public void modifyPlayerScoreSucceeded(String responseData) {
        Map<String, String> data = GsonConfiguration.gson.fromJson(responseData, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.parseInt(data.get(FieldsRequestName.GAME_ID));
        String username = data.get(FieldsRequestName.USERNAME);
        int score = Integer.parseInt(data.get(FieldsRequestName.PLAYER_SCORE));
        this.player.setScore(score);
    }

    public void getQuestionResponseSucceeded(String responseData) {
        Map<String, String> data = GsonConfiguration.gson.fromJson(responseData, CommunicationTypes.mapJsonTypeData);
        String username = data.get(FieldsRequestName.USERNAME);
        int score = Integer.parseInt(data.get(FieldsRequestName.PLAYER_SCORE));
        boolean state = Boolean.parseBoolean(data.get(FieldsRequestName.STATE));
        if (state) {
            if (username.equalsIgnoreCase(this.player.getUsername())) {
                this.player.setScore(score);
                ((GameController) this.controller).changeQuestionState("-fx-background-color: #11ec0d");
            }
            ((GameController) this.controller).updateScoreBoard(new Player(username, this.player.getGame(), score));
        } else {
            ((GameController) this.controller).changeQuestionState("-fx-background-color: #ec350d");
        }
        ((GameController) this.controller).setResponded();
    }

    public void createGameFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.CREATE_GAME, responseData);
    }

    public void joinGameFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.JOIN_GAME, responseData);
    }

    public void deleteGameFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.DELETE_GAME, responseData);
    }

    public void listOfNotStartedGameFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.LIST_OF_NOT_STARTED_GAME, responseData);
    }

    public void startGameFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.MODIFY_GAME_STATE, responseData);
    }

    public void modifyPlayerScoreFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.MODIFY_PLAYER_SCORE, responseData);

    }

    public void getQuestionResponseFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.GET_QUESTION_RESPONSE, responseData);

    }

    private void nextRoundSucceeded(String responseData) {
        NextRoundInformation nextRoundInformation = GsonConfiguration.gson.fromJson(responseData,
                NextRoundInformation.class);
        ((GameController) this.controller).initView(nextRoundInformation.getPlayers(),
                nextRoundInformation.getQuestion(), nextRoundInformation.getQuestionOrder());
    }

    private void nextRoundInformationFailed(String responseData) {
        this.controller.commandFailed(FailureMessages.NEXT_ROUND_INFORMATION, responseData);
    }

    private void gameFinishedSucceeded(String responseData) {
        ((GameController) this.controller).gameFinished();
    }

    private void gameFinishedFailed(String responseData) {

    }

    private void removeGameFromListOfAvailableGames(String responseData) {
        ((MainMenuController) this.controller).deleteGameFromList(Integer.parseInt(responseData));
    }

    // Functions that send the requests :
    public void createGame(boolean type, boolean state, int rounds, int players, int time_question, String username) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.IP_ADDRESS, ipAddress);
        requestData.put(FieldsRequestName.GAME_TYPE, String.valueOf(type));
        requestData.put(FieldsRequestName.ROUNDS, String.valueOf(rounds));
        requestData.put(FieldsRequestName.PLAYERS, String.valueOf(players));
        requestData.put(FieldsRequestName.TIME_QUESTION, String.valueOf(time_question));
        requestData.put(FieldsRequestName.STATE, String.valueOf(state));
        requestData.put(FieldsRequestName.USERNAME, username);
        Request createGame = new Request(NetCodes.CREATE_GAME,
                GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
        request(createGame);
    }

    public void deleteGame(int gameId) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(gameId));
        requestData.put(FieldsRequestName.USERNAME, this.player.getUsername());
        Request deleteGame = new Request(NetCodes.DELETE_GAME,
                GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
        request(deleteGame);
    }

    public void joinGame(int gameId, String username) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.IP_ADDRESS, ipAddress);
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(gameId));
        requestData.put(FieldsRequestName.USERNAME, username);
        Request joinGame = new Request(NetCodes.JOIN_GAME,
                GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
        request(joinGame);
    }

    public void leaveGame(int gameId, String username) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(gameId));
        requestData.put(FieldsRequestName.USERNAME, username);
        Request leaveGame = new Request(NetCodes.LEAVE_GAME,
                GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
        request(leaveGame);
    }

    public void listOfNotStartedGame() {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.IP_ADDRESS, ipAddress);
        Request lisOfNotStartedGame = new Request(NetCodes.LIST_OF_GAME_NOT_STARTED,
                GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
        request(lisOfNotStartedGame);
    }

    public void startGame() {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.USERNAME, this.player.getUsername());
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(this.player.getGame().getId()));
        requestData.put(FieldsRequestName.GAME_TYPE, String.valueOf(this.player.getGame().isImageGame()));
        requestData.put(FieldsRequestName.ROUNDS, String.valueOf(this.player.getGame().getRounds()));
        Request modifyGameState = new Request(NetCodes.START_GAME, GsonConfiguration.gson.toJson(requestData,
                CommunicationTypes.mapJsonTypeData));
        request(modifyGameState);
    }

    public void modifyPlayerScore() {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.USERNAME, this.player.getUsername());
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(this.player.getGame().getId()));
        requestData.put(FieldsRequestName.PLAYER_SCORE, String.valueOf(this.player.getScore()));
        Request modifyPlayerScore = new Request(NetCodes.MODIFY_SCORE, GsonConfiguration.gson.toJson(requestData,
                CommunicationTypes.mapJsonTypeData));
        request(modifyPlayerScore);
    }

    public void getQuestionResponse(int orderQuestion, String playerResponse) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.USERNAME, this.player.getUsername());
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(this.player.getGame().getId()));
        requestData.put(FieldsRequestName.PLAYER_SCORE, String.valueOf(this.player.getScore()));
        requestData.put(FieldsRequestName.PLAYER_RESPONSE, playerResponse);
        requestData.put(FieldsRequestName.QUESTION_ORDER, String.valueOf(orderQuestion));
        Request getQuestionResponse = new Request(NetCodes.GET_RESPONSE_FOR_QUESTION,
                GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
        request(getQuestionResponse);
    }

    public void nextRound(int questionOrder) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.QUESTION_ORDER, String.valueOf(questionOrder));
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(this.player.getGame().getId()));
        requestData.put(FieldsRequestName.USERNAME, this.player.getUsername());
        Request nextRound = new Request(NetCodes.NEXT_ROUND, GsonConfiguration.gson.toJson(requestData,
                CommunicationTypes.mapJsonTypeData));
        request(nextRound);
    }

    // Functions that don't do sql requests :

    public void gameFinished() {
        Map<String, String> requestData = new HashMap<>();
        requestData.put(FieldsRequestName.GAME_ID, String.valueOf(this.player.getGame().getId()));
        requestData.put(FieldsRequestName.USERNAME, this.player.getUsername());
        Request gameFinished = new Request(NetCodes.GAME_FINISHED, GsonConfiguration.gson.toJson(requestData,
                CommunicationTypes.mapJsonTypeData));
        request(gameFinished);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public AsynchronousSocketChannel getClient() {
        return client;
    }

    public void setClient(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public Player getPlayer() {
        return player;
    }

    public void request(Request request) {
        ByteBuffer buffer = ByteBuffer.wrap(GsonConfiguration.gson.toJson(request).getBytes());
        this.client.write(buffer, buffer, new ClientWriterCompletionHandler());
    }


}
