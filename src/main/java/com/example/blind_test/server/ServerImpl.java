package com.example.blind_test.server;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.database.repositories.PlayerRepository;
import com.example.blind_test.database.repositories.QuestionRepository;
import com.example.blind_test.exception.ChangeGameStateException;
import com.example.blind_test.exception.ListOfNotStartedGameException;
import com.example.blind_test.front.models.Game;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
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
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());

        } catch (ChangeGameStateException e) {
            Response response = new Response(NetCodes.CHANGE_GAME_STATE_FAILED, "change state failure");
            requestFailure(response, client);
        }

    }
//
//    public static void modifyPlayerScore(String data) {
//        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
//        int gameId = Integer.valueOf(requestData.get(FieldsRequestName.GAMEID));
//        String username = requestData.get(FieldsRequestName.USERNAME);
//        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
//        try {
//            Integer i = playerRepository.modifyPlayerScoreDB();
//
//
//        }
//
//
//    }

    public static void initListOfFunctions() {
        // initialisation of methods;
        listOfFunctions.put(NetCodes.LIST_OF_GAME_NOT_STARTED, ServerImpl::listOfNotStartedGame);
        listOfFunctions.put(NetCodes.CHANGE_GAME_STATE, ServerImpl::modifyGameState);
        //listOfFunctions.put(NetCodes.MODIFY_SCORE, ServerImpl::modifyPlayerScore);
    }


    public static void getQuestionResponse(String requestData){

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
