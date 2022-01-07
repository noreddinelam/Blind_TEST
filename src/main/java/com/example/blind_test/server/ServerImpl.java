package com.example.blind_test.server;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.database.repositories.PlayerRepository;
import com.example.blind_test.database.repositories.QuestionRepository;
import com.example.blind_test.exception.*;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.shared.CommunicationTypes;
import com.example.blind_test.shared.FieldsRequestName;
import com.example.blind_test.shared.NetCodes;
import com.example.blind_test.shared.Properties;
import com.example.blind_test.shared.communication.Credentials;
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
        int current_question = Integer.parseInt(requestData.get(FieldsRequestName.CURRENT_QUESTION));
        int rounds = Integer.parseInt(requestData.get(FieldsRequestName.ROUNDS));
        int players = Integer.parseInt(requestData.get(FieldsRequestName.PLAYERS));
        int timeQuestion = Integer.parseInt(requestData.get(FieldsRequestName.TIME_QUESTION));
        boolean state = Boolean.parseBoolean(requestData.get(FieldsRequestName.STATE));
        String username = requestData.get(FieldsRequestName.USERNAME);
        try {
            Player player = gameRepository.createGameDB(type, current_question, rounds
                    , players, timeQuestion, state,username);
            listOfPlayers.put(new Credentials(username,player.getGame().getId()),client);
            listOfGuests.remove(ipAddress);
            Response response = new Response(NetCodes.CREATE_GAME_SUCCEED,GsonConfiguration.gson.toJson(player));
            responseSucceed(client, response);
        } catch (CreateGameDBException e) {
            Response response = new Response(NetCodes.CREATE_GAME_FAILED, "Create game failure");
            requestFailure(response,client);
        }
    }

    private static void deleteGame(String data) throws GetPlayersOfGameException {
        logger.info("CREATE GAME INFO {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameID = Integer.parseInt(requestData.get(FieldsRequestName.GAMEID));
        List <Player> list = playerRepository.getPlayersOfGame(gameID);
        List <AsynchronousSocketChannel> clients=new ArrayList<>();
        for(Player player: list)
        {
            clients.add(listOfPlayers.get(new Credentials(player.getUsername(),gameID)));
        }
       
        try {
            gameRepository.deleteGameDB(gameID);
            Response response = new Response(NetCodes.DELETE_GAME_SUCCEED,"Game deleted!");
            for(AsynchronousSocketChannel client : clients)
            {
                responseSucceed(client,response);
            }
        } catch (Exception e) {
            Response response = new Response(NetCodes.DELETE_GAME_FAILED, "delete game failure");
            for(AsynchronousSocketChannel client : clients)
            {
                responseFailure(client,response);
            }
        }
    }

    private static void responseFailure(AsynchronousSocketChannel client, Response response) {
    }

    private static void responseSucceed(AsynchronousSocketChannel client, Response response) {
        String responseJson = GsonConfiguration.gson.toJson(response);
        ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
        client.write(attachment, attachment, new ServerWriterCompletionHandler());
        attachment.clear();
        ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
        client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
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
            Response response = new Response(NetCodes.LIST_OF_GAME_NOT_STARTED_SUCCEED, GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapListGameJsonTypeData));
            responseSucceed(client, response);
        } catch (ListOfNotStartedGameException e) {
            Response response = new Response(NetCodes.LIST_OF_GAME_NOT_STARTED_FAILED, "list of not started game failure");
            requestFailure(response, client);
        }
    }

    private static void modifyGameState(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.valueOf(requestData.get(FieldsRequestName.GAMEID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {
            Integer i = gameRepository.changeGameState(gameId);
            Map<String, String> responseData = new HashMap<>();
            responseData.put(FieldsRequestName.GAME_STATE, "true");
            responseData.put(FieldsRequestName.GAMEID, String.valueOf(gameId));
            Response response = new Response(NetCodes.CHANGE_GAME_STATE_SUCCEED, GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapJsonTypeData));
            responseSucceed(client, response);

        } catch (ChangeGameStateException e) {
            Response response = new Response(NetCodes.CHANGE_GAME_STATE_FAILED, "change state failure");
            requestFailure(response, client);
        }

    }

    public static void modifyPlayerScore(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.valueOf(requestData.get(FieldsRequestName.GAMEID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        int score = Integer.valueOf(requestData.get(FieldsRequestName.PLAYER_SCORE));
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {
            int newScore = score + 1;
            Map<String, String> responseData = new HashMap<>();
            int i = playerRepository.modifyScore(newScore, gameId, username);
            responseData.put(FieldsRequestName.GAMEID, String.valueOf(gameId));
            responseData.put(FieldsRequestName.USERNAME, username);
            responseData.put(FieldsRequestName.PLAYER_SCORE, String.valueOf(newScore));
            Response response = new Response(NetCodes.MODIFY_SCORE_SUCCEED, GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapJsonTypeData));
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (ModifyPlayerScoreDBException e) {
            Response response = new Response(NetCodes.MODIFY_SCORE_FAILED, "modify score failure");
            requestFailure(response, client);
        }
    }



    public static void initListOfFunctions() {
        // initialisation of methods;
        listOfFunctions.put(NetCodes.LIST_OF_GAME_NOT_STARTED, ServerImpl::listOfNotStartedGame);
        listOfFunctions.put(NetCodes.CHANGE_GAME_STATE, ServerImpl::modifyGameState);
        listOfFunctions.put(NetCodes.MODIFY_SCORE, ServerImpl::modifyPlayerScore);
    }


    public static void getQuestionResponse(String requestData) {

    }


    public static Consumer<String> getFunctionWithRequestCode(Request request) {
        return listOfFunctions.get(request.getNetCode());
    }

    public static void addGuestClients(AsynchronousSocketChannel client) throws IOException {
        listOfGuests.put(client.getRemoteAddress().toString().split(":")[1], client);
    }

    private static void requestFailure(Response response, AsynchronousSocketChannel client) {
        String responseJson = GsonConfiguration.gson.toJson(response);
        ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes());
        client.write(buffer, buffer, new ServerWriterCompletionHandler());
        ByteBuffer bufferReader = ByteBuffer.allocate(Properties.BUFFER_SIZE);
        client.read(bufferReader, bufferReader, new ServerReaderCompletionHandler());
    }

    private static void broadcastResponseClient(AsynchronousSocketChannel broadcastClient, Response broadcastResponse) {
        String responseJson = GsonConfiguration.gson.toJson(broadcastResponse);
        ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes());
        broadcastClient.write(buffer, buffer, new ServerWriterCompletionHandler());
    }

}
