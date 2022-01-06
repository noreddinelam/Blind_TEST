package com.example.blind_test.server;

import com.example.blind_test.database.repositories.GameRepository;
import com.example.blind_test.database.repositories.Repository;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.shared.*;
import com.example.blind_test.shared.Properties;
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

    private static final ConcurrentHashMap<String, AsynchronousSocketChannel> listOfClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AsynchronousSocketChannel> listOfGuests = new ConcurrentHashMap<>();
    private static final GameRepository repository = GameRepository.getRepository();
    private static final Mapper mapper = Mapper.getMapper();
    private static final Hashtable<String, Consumer<String>> listOfFunctions = new Hashtable<>();
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    private ServerImpl() {
    }

    public static void initListOfFunctions() {
        // initialisation of methods;
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
