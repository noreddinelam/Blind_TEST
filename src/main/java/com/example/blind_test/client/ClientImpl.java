package com.example.blind_test.client;


import com.example.blind_test.front.controllers.Controller;
import com.example.blind_test.front.controllers.MainMenuController;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.shared.CommunicationTypes;
import com.example.blind_test.shared.FieldsRequestName;
import com.example.blind_test.shared.NetCodes;
import com.example.blind_test.shared.Properties;
import com.example.blind_test.shared.communication.Request;
import com.example.blind_test.shared.communication.Response;
import com.example.blind_test.shared.gson_configuration.GsonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Hashtable;
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

    private ClientImpl(){}

    public static ClientImpl getUniqueInstanceClientImpl(){
        return clientImpl;
    }

    public static Consumer<String> getFunctionWithRequestCode(Response response) {
        return listOfFunctions.get(response.getNetCode());
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
    }

    public void createGameSucceeded(String responseData) {
        Player player = GsonConfiguration.gson.fromJson(responseData, Player.class);
        this.player = player;


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
        ByteBuffer buffer = ByteBuffer.wrap(GsonConfiguration.gson.toJson(createGame).getBytes());
        this.client.write(buffer, buffer, new ClientWriterCompletionHandler());
    }

    // Functions that don't do sql requests :

    public void setAsynchronousSocketChannel(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public void setMainMenuController(Controller controller) {
        this.controller = controller;
    }

    public AsynchronousSocketChannel getClient() {
        return client;
    }


}
