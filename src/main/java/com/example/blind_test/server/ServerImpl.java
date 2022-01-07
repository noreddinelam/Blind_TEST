package com.example.blind_test.server;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.database.repositories.PlayerRepository;
import com.example.blind_test.database.repositories.QuestionRepository;
import com.example.blind_test.exception.*;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.shared.CommunicationTypes;
import com.example.blind_test.shared.FieldsRequestName;
import com.example.blind_test.shared.NetCodes;
import com.example.blind_test.shared.Properties;
import com.example.blind_test.shared.communication.Credentials;
import com.example.blind_test.shared.communication.NextRoundInformation;
import com.example.blind_test.shared.communication.Request;
import com.example.blind_test.shared.communication.Response;
import com.example.blind_test.shared.gson_configuration.GsonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerImpl {

    private static final ConcurrentHashMap<Credentials, AsynchronousSocketChannel> listOfPlayers =
            new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AsynchronousSocketChannel> listOfGuests = new ConcurrentHashMap<>();
    private static final GameRepository gameRepository = GameRepository.getRepository();
    private static final QuestionRepository questionRepository = QuestionRepository.getRepository();
    private static final PlayerRepository playerRepository = PlayerRepository.getRepository();
    private static final Hashtable<String, Consumer<String>> listOfFunctions = new Hashtable<>();
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    private ServerImpl() {
    }

    private static void createGame(String data) {
        logger.info("CREATE GAME INFO {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String ipAddress = requestData.get(FieldsRequestName.IP_ADDRESS);
        AsynchronousSocketChannel client = listOfGuests.get(ipAddress);
        boolean type = Boolean.parseBoolean(requestData.get(FieldsRequestName.GAME_TYPE));
        int rounds = Integer.parseInt(requestData.get(FieldsRequestName.ROUNDS));
        int players = Integer.parseInt(requestData.get(FieldsRequestName.PLAYERS));
        int timeQuestion = Integer.parseInt(requestData.get(FieldsRequestName.TIME_QUESTION));
        boolean state = Boolean.parseBoolean(requestData.get(FieldsRequestName.STATE));
        String username = requestData.get(FieldsRequestName.USERNAME);
        try {
            Player player = gameRepository.createGameDB(type, rounds
                    , players, timeQuestion, state, username);
            listOfPlayers.put(new Credentials(username, player.getGame().getId()), client);
            listOfGuests.remove(ipAddress);
            Response response = new Response(NetCodes.CREATE_GAME_SUCCEED, GsonConfiguration.gson.toJson(player));
            response(response, client);
        } catch (CreateGameDBException e) {
            Response response = new Response(NetCodes.CREATE_GAME_FAILED, "Create game failure");
            response(response, client);
        }
    }


    private static void deleteGame(String data) throws GetPlayersOfGameException {
        logger.info("CREATE GAME INFO {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameID = Integer.parseInt(requestData.get(FieldsRequestName.GAMEID));
        List<Player> list = playerRepository.getPlayersOfGame(gameID);
        List<AsynchronousSocketChannel> clients = new ArrayList<>();
        for (Player player : list) {
            clients.add(listOfPlayers.get(new Credentials(player.getUsername(), gameID)));
        }
        try {
            gameRepository.deleteGameDB(gameID);
            Response response = new Response(NetCodes.DELETE_GAME_SUCCEED, "Game deleted!");
            for (AsynchronousSocketChannel client : clients) {
                response(response, client);
            }
        } catch (Exception e) {
            Response response = new Response(NetCodes.DELETE_GAME_FAILED, "delete game failure");
            for (AsynchronousSocketChannel client : clients) {
                response(response, client);
            }
        }
    }

    private static void joinGame(String data) {
        logger.info("JOIN GAME INFO {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String ipAddress = requestData.get(FieldsRequestName.IP_ADDRESS);
        AsynchronousSocketChannel clientJoin = listOfGuests.get(ipAddress);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAMEID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        try {
            List<Player> list = playerRepository.getPlayersOfGame(gameId);
            Player player = gameRepository.joinGameDB(gameId, username);
            Response response = new Response(NetCodes.JOIN_GAME_SUCCEED, GsonConfiguration.gson.toJson(player));
            listOfGuests.remove(ipAddress);
            listOfPlayers.put(new Credentials(username, player.getGame().getId()), clientJoin);
            response(response, clientJoin);
            Response aPlayerHasJoined = new Response(NetCodes.JOIN_GAME_BROADCAST_SUCCEED,
                    GsonConfiguration.gson.toJson(player));
            for (Player playerOther : list) {
                if (!playerOther.getUsername().equalsIgnoreCase(username))
                    response(aPlayerHasJoined, listOfPlayers.get(new Credentials(playerOther.getUsername(), gameId)));
            }
        } catch (PlayerAlreadyExists | GameIsFullException | JoinGameDBException | GetGameDBException | GetNbPlayersInGameException | AddNewPlayerDBException | GetPlayersOfGameException e) {
            Response response = new Response(NetCodes.JOIN_GAME_FAILED, "Join game failure");
            response(response, clientJoin);
        }
    }


    private static void listOfNotStartedGame(String data) {
        logger.info("list of started game {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String ipAddress = requestData.get(FieldsRequestName.IP_ADDRESS);
        AsynchronousSocketChannel client = listOfGuests.get(ipAddress);
        try {
            List<Game> resultGame = gameRepository.listOfNotStartedGameDb();
            Map<String, List<Game>> responseData = new HashMap<>();
            responseData.put(FieldsRequestName.LISTGAMES, resultGame);
            Response response = new Response(NetCodes.LIST_OF_GAME_NOT_STARTED_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapListGameJsonTypeData));
            response(response, client);
        } catch (ListOfNotStartedGameException e) {
            Response response = new Response(NetCodes.LIST_OF_GAME_NOT_STARTED_FAILED, "list of not started game " +
                    "failure");
            response(response, client);
        }
    }

    private static void modifyGameState(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.valueOf(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {
            Integer i = gameRepository.changeGameState(gameId);
            Map<String, String> responseData = new HashMap<>();
            responseData.put(FieldsRequestName.GAME_STATE, "true");
            responseData.put(FieldsRequestName.GAMEID, String.valueOf(gameId));
            Response response = new Response(NetCodes.CHANGE_GAME_STATE_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapJsonTypeData));
            response(response, client);
        } catch (ChangeGameStateException e) {
            Response response = new Response(NetCodes.CHANGE_GAME_STATE_FAILED, "change state failure");
            response(response, client);
        }
    }

    public static void modifyPlayerScore(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.valueOf(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        int score = Integer.valueOf(requestData.get(FieldsRequestName.PLAYER_SCORE));
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {
            int newScore = score + 1;
            Map<String, String> responseData = new HashMap<>();
            int i = playerRepository.modifyScore(newScore, gameId, username);
            responseData.put(FieldsRequestName.GAME_ID, String.valueOf(gameId));
            responseData.put(FieldsRequestName.USERNAME, username);
            responseData.put(FieldsRequestName.PLAYER_SCORE, String.valueOf(newScore));
            Response response = new Response(NetCodes.MODIFY_SCORE_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapJsonTypeData));
            response(response, client);
        } catch (ModifyPlayerScoreDBException e) {
            Response response = new Response(NetCodes.MODIFY_SCORE_FAILED, "modify score failure");
            response(response, client);
        }
    }

    public static void getQuestionResponse(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.valueOf(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        int idCurrentQuestion = Integer.valueOf(requestData.get(FieldsRequestName.CURRENT_QUESTION));
        String playerResponse = requestData.get(FieldsRequestName.PLAYER_RESPONSE);
        int score = Integer.valueOf(requestData.get(FieldsRequestName.PLAYER_SCORE));
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {

            if (questionRepository.verifyQuestionState(idCurrentQuestion)==0) {
                Question question = QuestionRepository.getRepository().getQuestion(idCurrentQuestion);
                String questionResponse = question.getResponse();
                Map<String, String> responseData = new HashMap<>();
                responseData.put(FieldsRequestName.GAME_ID, String.valueOf(gameId));
                responseData.put(FieldsRequestName.USERNAME, username);
                responseData.put(FieldsRequestName.CURRENT_QUESTION, questionResponse);

                if (questionResponse.equalsIgnoreCase(playerResponse)) {
                    score = score + 1;
                    int i = playerRepository.modifyScore(score, gameId, username);
                    questionRepository.chnageQuestionState(idCurrentQuestion);
                    responseData.put(FieldsRequestName.STATE, "true");
                } else {
                    responseData.put(FieldsRequestName.STATE, "false");
                }
                Response response = new Response(NetCodes.GET_RESPONSE_FOR_QUESTION_SUCCEED, GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapJsonTypeData));
                response(response, client);
            }
            else {
                Response response = new Response(NetCodes.CHANGE_GAME_STATE_FAILED, "the question responded");
                response(response, client);
            }

        } catch (Exception e) {
            Response response = new Response(NetCodes.GET_RESPONSE_FOR_QUESTION_FAILED, "get response for question failure");
            response(response, client);
        }
    }

    public static void nextRoundInformation(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAMEID));
        int questionOrder = Integer.parseInt(requestData.get(FieldsRequestName.CURRENT_QUESTION));
        try {
            List<Player> list = playerRepository.getPlayersOfGame(gameId);
            Question nextQuestion = questionRepository.getQuestionByOrder(gameId, questionOrder);
            NextRoundInformation nextRoundInformation = new NextRoundInformation(list, nextQuestion);
            Response response = new Response(NetCodes.NEXT_ROUND_SUCCEEDED,GsonConfiguration.gson.toJson(nextRoundInformation));
            for (Player player : list){
                response(response, listOfPlayers.get(new Credentials(player.getUsername(), gameId)));
            }
        } catch (GetPlayersOfGameException e) {
            e.printStackTrace();
        }
    }

    public static void initListOfFunctions() {
        // initialisation of methods;
        listOfFunctions.put(NetCodes.LIST_OF_GAME_NOT_STARTED, ServerImpl::listOfNotStartedGame);
        listOfFunctions.put(NetCodes.CHANGE_GAME_STATE, ServerImpl::modifyGameState);
        listOfFunctions.put(NetCodes.MODIFY_SCORE, ServerImpl::modifyPlayerScore);
        listOfFunctions.put(NetCodes.MODIFY_SCORE, ServerImpl::createGame);
        listOfFunctions.put(NetCodes.MODIFY_SCORE, ServerImpl::getQuestionResponse);
    }

    public static Consumer<String> getFunctionWithRequestCode(Request request) {
        return listOfFunctions.get(request.getNetCode());
    }

    public static void addGuestClients(AsynchronousSocketChannel client) throws IOException {
        listOfGuests.put(client.getRemoteAddress().toString().split(":")[1], client);
    }

    private static void response(Response response, AsynchronousSocketChannel client) {
        String responseJson = GsonConfiguration.gson.toJson(response);
        ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
        client.write(attachment, attachment, new ServerWriterCompletionHandler());
        attachment.clear();
        ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
        client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
    }

    private static void broadcastResponseClient(AsynchronousSocketChannel broadcastClient, Response broadcastResponse) {
        String responseJson = GsonConfiguration.gson.toJson(broadcastResponse);
        ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes());
        broadcastClient.write(buffer, buffer, new ServerWriterCompletionHandler());
    }

}
