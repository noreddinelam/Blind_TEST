package com.example.blind_test.server;

import Exceptions.*;
import com.example.blind_test.shared.communication.Request;
import database.Repository;
import database.SQLTablesInformation;
import models.Channel;
import models.Message;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerImpl {

    private static final ConcurrentHashMap<String, AsynchronousSocketChannel> listOfClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AsynchronousSocketChannel> listOfGuests = new ConcurrentHashMap<>();
    private static final Repository repository = Repository.getRepository();
    private static final Mapper mapper = Mapper.getMapper();
    private static final Hashtable<String, Consumer<String>> listOfFunctions = new Hashtable<>();
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    private ServerImpl() {
    }

    public static void logout(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String guest = requestData.get(FieldsRequestName.guest);
        AsynchronousSocketChannel client = listOfClients.get(username);
        listOfClients.remove(username);
        listOfGuests.put(guest, client);
        Response response = new Response(NetCodes.LOGOUT_SUCCEED,
                "Logout Succeed");
        String responseJson = GsonConfiguration.gson.toJson(response);
        ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
        client.write(attachment, attachment, new ServerWriterCompletionHandler());
        ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
        client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
    }

    public static void connect(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String guest = requestData.get(FieldsRequestName.guest);
        String username = requestData.get(FieldsRequestName.userName);
        String password = requestData.get(FieldsRequestName.password);
        AsynchronousSocketChannel client = listOfGuests.get(guest);
        try {
            ResultSet rs = repository.connectionDB(username, password).orElseThrow(ConnectionException::new);
            if (rs.next()) {
                Response response = new Response(NetCodes.CONNECT_SUCCEED,
                        GsonConfiguration.gson.toJson(new User(username, password)));
                String responseJson = GsonConfiguration.gson.toJson(response);
                ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
                listOfClients.put(username, client);
                listOfGuests.remove(guest);
                client.write(attachment, attachment, new ServerWriterCompletionHandler());
                attachment.clear();
                ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
                client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
            } else {
                throw new ConnectionException();
            }
        } catch (ConnectionException e) {
            Response response = new Response(NetCodes.CONNECT_FAILED, "Connection FAILED " +
                    "! Please enter a valid credentials ");
            requestFailure(response, client);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void register(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String guest = requestData.get(FieldsRequestName.guest);
        String username = requestData.get(FieldsRequestName.userName);
        String password = requestData.get(FieldsRequestName.password);
        AsynchronousSocketChannel client = listOfGuests.get(guest);
        try {
            repository.registerDB(username, password).orElseThrow(RegisterException::new);
            Response response = new Response(NetCodes.REGISTER_SUCCEED,
                    GsonConfiguration.gson.toJson(new User(username)));
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            listOfClients.put(username, client);
            listOfGuests.remove(guest);
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (RegisterException e) {
            Response response = new Response(NetCodes.REGISTER_FAILED, "Registration FAILED ! username already exists" +
                    " !");
            requestFailure(response, client);
        }
    }

    public static void createChannel(String data) {

        Channel requestData = GsonConfiguration.gson.fromJson(data, Channel.class);
        AsynchronousSocketChannel client = listOfClients.get(requestData.getAdmin().getUsername());
        logger.info("Create channel data received {}", requestData);
        try {
            repository.createChannelDB(requestData).orElseThrow(CreateChannelException::new);
            repository.insertAdminClientChannelTableDB(requestData.getChannelName(),
                    requestData.getAdmin().getUsername()).orElseThrow(InsertAdminClientChannelTableException::new);
            Response response = new Response(NetCodes.CREATE_CHANNEL_SUCCEED,
                    GsonConfiguration.gson.toJson(requestData));
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            logger.info("username {}", requestData.getAdmin().getUsername());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (CreateChannelException e) {
            Response response = new Response(NetCodes.CREATE_CHANNEL_FAILED, "Channel creation failed");
            requestFailure(response, client);
        } catch (InsertAdminClientChannelTableException e) {
            e.printStackTrace();
        }
    }

    public static void joinChannel(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        String admin = requestData.get(FieldsRequestName.adminName);
        logger.info("joining channel {} ", data);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            Response response;
            ResultSet verifyStatusChannelResultSet =
                    repository.verifyChannelStatusDB(channelName).orElseThrow(VerifyStatusChannelException::new);
            if (verifyStatusChannelResultSet.next()) {
                boolean isPublic =
                        verifyStatusChannelResultSet.getBoolean(SQLTablesInformation.channelIsPublicChannelColumn);
                if (isPublic) {
                    ResultSet verifyResultSet =
                            repository.verifyJoinChannelDB(channelName, username).orElseThrow(VerifyJoinChannelException::new);
                    if (!verifyResultSet.next()) {
                        repository.joinChannelDB(channelName, username).orElseThrow(JoinChannelException::new);
                        ResultSet resultSet =
                                repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
                        Response broadcastResponse = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_SUCCEED,
                                data);
                        response = new Response(NetCodes.JOIN_CHANNEL_SUCCEED,
                                GsonConfiguration.gson.toJson(new Channel(new User(admin), channelName, "", true)));
                        String broadcastUsername;
                        AsynchronousSocketChannel broadcastClient;
                        while (resultSet.next()) {
                            broadcastUsername = resultSet.getString("username");
                            broadcastClient = listOfClients.get(broadcastUsername);
                            if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                                broadcastResponseClient(broadcastClient, broadcastResponse);
                        }
                    } else {
                        logger.info("user already joined the channel");
                        response = new Response(NetCodes.JOIN_CHANNEL_FAILED, "Channel joining failed");
                    }

                } else {
                    logger.info("Channel is private, request sent to the admin");
                    int res =
                            repository.joinChannelStatusRequestDB(admin, channelName, username).orElseThrow(ResponseJoinChannelException::new);
                    if (res != 0)
                        response = new Response(NetCodes.JOIN_PRIVATE_CHANNEL, "Your request is sent to the admin to " +
                                "join the channel");
                    else {
                        logger.info("Request already set ");
                        response = new Response(NetCodes.REQUEST_JOIN_FAILED, "request is already set");
                    }
                }
            } else {
                response = new Response(NetCodes.JOIN_CHANNEL_FAILED, "joining channel failed");
            }
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            logger.info("username {}", username);
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (JoinChannelException | VerifyStatusChannelException | ResponseJoinChannelException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_FAILED, "joining channel failed");
            requestFailure(response, client);
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_FAILED, "broadcasting message error");
            requestFailure(response, client);
        } catch (VerifyJoinChannelException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_FAILED, "you are already in the channel");
            requestFailure(response, client);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void leaveChannel(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        logger.info("leaving channel {} ", data);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet resultSet =
                    repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
            repository.leaveChannelDB(channelName, username).orElseThrow(LeaveChannelException::new);
            Response response = new Response(NetCodes.LEAVE_CHANNEL_SUCCEED, channelName);
            Response broadcastResponse = new Response(NetCodes.LEAVE_CHANNEL_BROADCAST_SUCCEED, data);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
            String broadcastUsername;
            AsynchronousSocketChannel broadcastClient;
            while (resultSet.next()) {
                broadcastUsername = resultSet.getString("username");
                broadcastClient = listOfClients.get(broadcastUsername);
                if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                    broadcastResponseClient(broadcastClient, broadcastResponse);
            }
        } catch (LeaveChannelException e) {
            Response response = new Response(NetCodes.LEAVE_CHANNEL_FAILED, "Leaving channel failed");
            requestFailure(response, client);
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.LEAVE_CHANNEL_BROADCAST_FAILED, "broadcasting message error");
            requestFailure(response, client);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //TODO : there is enhancements in future (didn't handle the return of deleteMessageDB )
    public static void deleteMessage(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int idMessage = Integer.parseInt(requestData.get(FieldsRequestName.messageID));
        String channelName = requestData.get(FieldsRequestName.channelName);
        String username = requestData.get(FieldsRequestName.userName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            Response response = new Response(NetCodes.DELETE_MESSAGE_SUCCEED, data);
            repository.deleteMessageDB(idMessage).orElseThrow(DeleteMessageException::new);
            ResultSet resultSet =
                    repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
            Response broadcastResponse = new Response(NetCodes.DELETE_MESSAGE_BROADCAST_SUCCEED, data);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
            String broadcastUsername;
            AsynchronousSocketChannel broadcastClient;
            while (resultSet.next()) {
                broadcastUsername = resultSet.getString("username");
                broadcastClient = listOfClients.get(broadcastUsername);
                if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                    broadcastResponseClient(broadcastClient, broadcastResponse);
            }

        } catch (DeleteMessageException e) {
            Response response = new Response(NetCodes.DELETE_MESSAGE_FAILED, "Message deletion failed");
            requestFailure(response, client);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_FAILED, "broadcasting deletion message " +
                    "error");
            requestFailure(response, client);
        }
    }

    public static void modifyMessage(String data) {
        logger.info("modify message {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int idMessage = Integer.valueOf(requestData.get(FieldsRequestName.messageID));
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            int modLines =
                    repository.modifyMessageDB(requestData.get(FieldsRequestName.newMessageContent), idMessage).orElseThrow(ModifyMessageException::new);
            ResultSet resultSet =
                    repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
            Response broadcastResponse = new Response(NetCodes.MODIFY_MESSAGE_BROADCAST_SUCCEEDED, data);
            Response response = new Response(NetCodes.MODIFY_MESSAGE_SUCCEED, data);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
            String broadcastUsername;
            AsynchronousSocketChannel broadcastClient;
            while (resultSet.next()) {
                broadcastUsername = resultSet.getString("username");
                broadcastClient = listOfClients.get(broadcastUsername);
                if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                    broadcastResponseClient(broadcastClient, broadcastResponse);
            }
        } catch (ModifyMessageException e) {
            Response response = new Response(NetCodes.MODIFY_MESSAGE_FAILED, "Message modification failed");
            requestFailure(response, client);
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_FAILED, "broadcasting message error");
            requestFailure(response, client);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void deleteChannel(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String channelName = requestData.get(FieldsRequestName.channelName);
        String username = requestData.get(FieldsRequestName.userName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            Response response = new Response(NetCodes.DELETE_CHANNEL_SUCCEED, data);
            Response broadcastResponse = new Response(NetCodes.DELETE_CHANNEL_BROADCAST_SUCCEEDED, channelName);
            ResultSet resultSet =
                    repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
            int result = repository.deleteChannelDB(channelName).orElseThrow(DeleteChannelException::new);
            if (result != 0) {
                ByteBuffer buffer = ByteBuffer.wrap(GsonConfiguration.gson.toJson(response).getBytes());
                client.write(buffer, buffer, new ServerWriterCompletionHandler());
                buffer.clear();
                ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
                client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
                String broadcastUsername;
                AsynchronousSocketChannel broadcastClient;
                while (resultSet.next()) {
                    broadcastUsername = resultSet.getString("username");
                    broadcastClient = listOfClients.get(broadcastUsername);
                    if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                        broadcastResponseClient(broadcastClient, broadcastResponse);
                }
            } else throw new DeleteChannelException();
        } catch (DeleteChannelException e) {
            Response response = new Response(NetCodes.DELETE_CHANNEL_FAILED, "Channel deletion failed");
            requestFailure(response, client);
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_FAILED, "broadcasting deletion channel " +
                    "error");
            requestFailure(response, client);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void deleteUserFromMyChannel(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        String admin = requestData.get(FieldsRequestName.adminName);
        AsynchronousSocketChannel adminClient = listOfClients.get(admin);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            Response response = new Response(NetCodes.DELETE_USER_FROM_CHANNEL_SUCCEED, data);
            ResultSet resultSet =
                    repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
            repository.deleteUserFromMyChannelDB(channelName, username).orElseThrow(DeleteUserFromMyChannelException::new);
            Response broadcastResponse = new Response(NetCodes.DELETE_USER_FROM_CHANNEL_BRODCAST_SUCCEED, data);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            if (client != null)
                client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            adminClient.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
            String broadcastUsername;
            AsynchronousSocketChannel broadcastClient;
            while (resultSet.next()) {
                broadcastUsername = resultSet.getString("username");
                broadcastClient = listOfClients.get(broadcastUsername);
                if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                    broadcastResponseClient(broadcastClient, broadcastResponse);
            }

        } catch (DeleteUserFromMyChannelException e) {
            Response response = new Response(NetCodes.DELETE_USER_FROM_CHANNEL_FAILED, "DELETE USER failed");
            requestFailure(response, client);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_FAILED, "broadcasting deletion user " +
                    "error");
            requestFailure(response, client);
            ;
        }
    }

    public static void modifyChannel(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        String newChannelName = requestData.get(FieldsRequestName.newChannelName);
        boolean isPublic = requestData.get(FieldsRequestName.channelPublic).equalsIgnoreCase("true");
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            Response broadcastResponse = new Response(NetCodes.MODIFY_CHANNEL_BROADCAST_SUCCEED, data);
            ResultSet resultSet =
                    repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
            Response response = new Response(NetCodes.MODIFY_CHANNEL_SUCCEED, data);
            repository.modifyChannelDB(newChannelName, isPublic, channelName).orElseThrow(ModifyChannelException::new);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
            String broadcastUsername;
            AsynchronousSocketChannel broadcastClient;
            while (resultSet.next()) {
                broadcastUsername = resultSet.getString("username");
                broadcastClient = listOfClients.get(broadcastUsername);
                if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                    broadcastResponseClient(broadcastClient, broadcastResponse);
            }
        } catch (ModifyChannelException e) {
            Response response = new Response(NetCodes.MODIFY_CHANNEL_FAILED, "Modify channel failed");
            requestFailure(response, client);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.MODIFY_CHANNEL_BROADCAST_FAILED, "broadcasting channel error");
            requestFailure(response, client);
        }
    }

//    public static void modifyChannelStatus(String data) {
//        Channel requestData = GsonConfiguration.gson.fromJson(data, Channel.class);
//        AsynchronousSocketChannel client = listOfClients.get(requestData.getAdmin().getUsername());
//        try {
//            Response response = new Response(NetCodes.MODIFY_CHANNEL_NAME_SUCCEED, data);
//            repository.modifyChannelStatusDB(requestData.isPublic(), requestData.getChannelName()).orElseThrow
//            (ModifyChannelStatusException::new);
//            String responseJson = GsonConfiguration.gson.toJson(response);
//            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
//            client.write(attachment, attachment, new ServerWriterCompletionHandler());
//            attachment.clear();
//            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
//            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
//        } catch (ModifyChannelStatusException e) {
//            e.printStackTrace();
//            Response response = new Response(NetCodes.MODIFY_CHANNEL_STATUS_FAILED, "Modify Channel Status failed");
//            requestFailure(response, client);
//        }
//    }

    public static void listOfRequests(String data) {
        logger.info("list of requests {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.adminName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet resultSet =
                    repository.responseJoinChannelRequestDB(username).orElseThrow(ResponseJoinChannelException::new);
            List<Map<String, String>> requests = new ArrayList<>();
            Map<String, String> usernameChannelName;
            while (resultSet.next()) {
                usernameChannelName = new HashMap<>();
                usernameChannelName.put(FieldsRequestName.channelName,
                        resultSet.getString(SQLTablesInformation.requestChannelName));
                usernameChannelName.put(FieldsRequestName.userName,
                        resultSet.getString(SQLTablesInformation.requestUsername));
                requests.add(usernameChannelName);
            }
            String responseData = GsonConfiguration.gson.toJson(requests,
                    CommunicationTypes.listMapChannelUsernameTypeData);
            Response response = new Response(NetCodes.LIST_REQUEST_JOIN_CHANNEL_SUCCEED, responseData);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ResponseJoinChannelException e) {
            Response response = new Response(NetCodes.LIST_REQUEST_JOIN_CHANNEL_FAILED, "List requests to join " +
                    "channel failed");
            requestFailure(response, client);
        }
    }

    public static void responseRequests(String data) {
        logger.info("response to join channel {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        String acceptString = requestData.get(FieldsRequestName.accept);
        String adminChannel = requestData.get(FieldsRequestName.adminName);
        AsynchronousSocketChannel adminUser = listOfClients.get(adminChannel);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            if (acceptString.equalsIgnoreCase("true")) {
                repository.joinChannelDB(channelName, username).orElseThrow(JoinChannelException::new);
                ResultSet resultSet =
                        repository.fetchAllUsersWithChannelName(channelName).orElseThrow(FetchAllUsersWithChannelNameException::new);
                Map<String, String> broadcast = new HashMap<>();
                broadcast.put(FieldsRequestName.userName, username);
                broadcast.put(FieldsRequestName.channelName, channelName);
                Response broadcastResponse = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_SUCCEED,
                        GsonConfiguration.gson.toJson(broadcast, CommunicationTypes.mapJsonTypeData));
                Channel joinedChannel = new Channel(new User(adminChannel), channelName, "", false);
                Response responseClient = new Response(NetCodes.JOIN_CHANNEL_SUCCEED,
                        GsonConfiguration.gson.toJson(joinedChannel));
                String responseJson = GsonConfiguration.gson.toJson(responseClient);
                ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
                if (client != null) {
                    client.write(attachment, attachment, new ServerWriterCompletionHandler());
                }
                attachment.clear();
                String broadcastUsername;
                AsynchronousSocketChannel broadcastClient;
                while (resultSet.next()) {
                    broadcastUsername = resultSet.getString("username");
                    broadcastClient = listOfClients.get(broadcastUsername);
                    if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                        broadcastResponseClient(broadcastClient, broadcastResponse);
                }
            } else {
                if (client != null) {
                    Response response = new Response(NetCodes.JOIN_CHANNEL_FAILED, "join channel refused");
                    String responseJson = GsonConfiguration.gson.toJson(response);
                    ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
                    client.write(attachment, attachment, new ServerWriterCompletionHandler());
                    attachment.clear();
                }
            }
            repository.deleteRequestJoinChannelDB(channelName, username).orElseThrow(DeleteRequestJoinChannelException::new);
            Response responseAdmin = new Response(NetCodes.RESPONSE_JOIN_SUCCEED,
                    GsonConfiguration.gson.toJson(requestData, CommunicationTypes.mapJsonTypeData));
            String responseJsonAdmin = GsonConfiguration.gson.toJson(responseAdmin);
            ByteBuffer adminBuffer = ByteBuffer.wrap(responseJsonAdmin.getBytes());
            adminUser.write(adminBuffer, adminBuffer, new ServerWriterCompletionHandler());
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            adminUser.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JoinChannelException | DeleteRequestJoinChannelException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_FAILED, "joining channel failed");
            requestFailure(response, client);
        } catch (FetchAllUsersWithChannelNameException e) {
            Response response = new Response(NetCodes.JOIN_CHANNEL_BROADCAST_FAILED, "broadcasting message error");
            requestFailure(response, client);
        }
    }

    public static void listChannelsInServer(String data) {
        logger.info("list of channel in the server {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet resultSet = repository.listChannelsInServerDB().orElseThrow(ListOfUserInChannelException::new);
            List<Channel> channels = mapper.resultSetToChannel(resultSet);
            Map<String, List<Channel>> responseData = new HashMap<>();
            responseData.put(FieldsRequestName.listChannels, channels);
            Response response = new Response(NetCodes.LIST_CHANNELS_IN_SERVER_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapListChannelJsonTypeData));
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ListOfUserInChannelException e) {
            Response response = new Response(NetCodes.LIST_CHANNELS_IN_SERVER_FAILED, "list of channels failed");
            requestFailure(response, client);
        }


    }

    public static void listOfUserInChannel(String data) {
        logger.info("list of user in channel {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet resultSet =
                    repository.listOfUserInChannelDB(channelName).orElseThrow(ListOfUserInChannelException::new);
            List<User> users = mapper.resultSetToUser(resultSet);
            Map<String, List<User>> responseData = new HashMap<>();
            responseData.put(channelName, users);
            Response response = new Response(NetCodes.LIST_OF_USER_IN_CHANNEL_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapListUserJsonTypeData));
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ListOfUserInChannelException e) {
            Response response = new Response(NetCodes.LIST_OF_USER_IN_CHANNEL_FAILED, "list of user in channel failed");
            requestFailure(response, client);
        }

    }

    public static void listOfMessageInChannel(String data) {
        logger.info("list of message in channel {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        String channelName = requestData.get(FieldsRequestName.channelName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet resultSet =
                    repository.listOfMessageInChannelDB(channelName).orElseThrow(ListOfMessageInChannelException::new);
            List<Message> messages = mapper.resultSetToMessage(resultSet);
            if (messages.isEmpty()) {
                messages.add(new Message(new Channel(channelName)));
            }
            Map<String, List<Message>> responseData = new HashMap<>();
            responseData.put(FieldsRequestName.listMessages, messages);
            Response response = new Response(NetCodes.List_Of_MESSAGE_IN_CHANNEL_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapListMessageJsonTypeData));
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
            client.write(attachment, attachment, new ServerWriterCompletionHandler());
            attachment.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ListOfMessageInChannelException e) {
            e.printStackTrace();
            Response response = new Response(NetCodes.List_Of_MESSAGE_IN_CHANNEL_FAILED, "list of message in channel " +
                    "failed");
            requestFailure(response, client);
        }
    }

    public static void consumeMessage(String data) {
        Message messageReceived = GsonConfiguration.gson.fromJson(data, Message.class);
        String channelName = messageReceived.getChannel().getChannelName();
        String username = messageReceived.getUser().getUsername();
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet generatedKeys = repository.addMessageDB(messageReceived).orElseThrow(AddMessageException::new);
            ResultSet result =
                    repository.listOfUserInChannelDB(channelName).orElseThrow(ListOfUserInChannelException::new);
            if (generatedKeys.next()) {
                messageReceived.setId(generatedKeys.getInt(1));
                Response responseSucceed = new Response(NetCodes.MESSAGE_CONSUMED,
                        GsonConfiguration.gson.toJson(messageReceived));
                Response broadcastResponse = new Response(NetCodes.MESSAGE_BROADCAST_SUCCEED,
                        GsonConfiguration.gson.toJson(messageReceived));
                ByteBuffer buffer = ByteBuffer.wrap(GsonConfiguration.gson.toJson(responseSucceed).getBytes());
                client.write(buffer, buffer, new ServerWriterCompletionHandler());
                buffer.clear();
                ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
                client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
                AsynchronousSocketChannel broadcastClient;
                String broadcastUsername;
                while (result.next()) {
                    broadcastUsername = result.getString(SQLTablesInformation.clientChannelUsernameColumn);
                    broadcastClient = listOfClients.get(broadcastUsername);
                    if (broadcastClient != null && !broadcastUsername.equalsIgnoreCase(username))
                        broadcastResponseClient(broadcastClient, broadcastResponse);
                }
            } else throw new AddMessageException();
        } catch (AddMessageException e) {
            Response response = new Response(NetCodes.MESSAGE_CONSUMPTION_ERROR, "Message consumption error");
            requestFailure(response, client);
        } catch (ListOfUserInChannelException e) {
            Response response = new Response(NetCodes.MESSAGE_BROADCAST_FAILED, "Message broadcast failed");
            requestFailure(response, client);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void listOfJoinedChannels(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet channels = repository.listOfJoinedChannels(username).orElseThrow(ListOfJoinedChannels::new);
            Map<String, List<Channel>> listOfChannels = new HashMap<>();
            List<Channel> joinedChannels = new ArrayList<>();
            while (channels.next()) {
                joinedChannels.add(new Channel(new User(channels.getString(SQLTablesInformation.channelAdminUsernameColumn)),
                        channels.getString(SQLTablesInformation.channelNameColumn), "",
                        channels.getBoolean(SQLTablesInformation.channelIsPublicChannelColumn)));
            }
            listOfChannels.put(FieldsRequestName.listChannels, joinedChannels);
            String dataJson = GsonConfiguration.gson.toJson(listOfChannels,
                    CommunicationTypes.mapListChannelJsonTypeData);
            Response response = new Response(NetCodes.LIST_OF_JOINED_CHANNELS_SUCCEEDED, dataJson);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes());
            client.write(buffer, buffer, new ServerWriterCompletionHandler());
            buffer.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (ListOfJoinedChannels e) {
            Response response = new Response(NetCodes.LIST_OF_JOINED_CHANNELS_FAILED, "List of joined channels failed" +
                    " !");
            requestFailure(response, client);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void listOfUnJoinedChannels(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String username = requestData.get(FieldsRequestName.userName);
        AsynchronousSocketChannel client = listOfClients.get(username);
        try {
            ResultSet channels =
                    repository.listOfUnJoinedChannelsDB(username).orElseThrow(listOfUnJoinedChannelsException::new);
            Map<String, List<Channel>> listOfChannels = new HashMap<>();
            List<Channel> unJoinedChannels = new ArrayList<>();
            while (channels.next()) {
                unJoinedChannels.add(new Channel(new User(channels.getString(SQLTablesInformation.channelAdminUsernameColumn)),
                        channels.getString(SQLTablesInformation.channelNameColumn), "",
                        channels.getBoolean(SQLTablesInformation.channelIsPublicChannelColumn)));
            }
            listOfChannels.put(FieldsRequestName.listChannels, unJoinedChannels);
            String dataJson = GsonConfiguration.gson.toJson(listOfChannels,
                    CommunicationTypes.mapListChannelJsonTypeData);
            Response response = new Response(NetCodes.LIST_OF_UN_JOINED_CHANNELS_SUCCEEDED, dataJson);
            String responseJson = GsonConfiguration.gson.toJson(response);
            ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes());
            client.write(buffer, buffer, new ServerWriterCompletionHandler());
            buffer.clear();
            ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (listOfUnJoinedChannelsException e) {
            e.printStackTrace();
        }
    }

    public static void initListOfFunctions() {
        // initialisation of methods;
        listOfFunctions.put(NetCodes.LOGOUT, ServerImpl::logout);
        listOfFunctions.put(NetCodes.CONNECTION, ServerImpl::connect);
        listOfFunctions.put(NetCodes.REGISTER, ServerImpl::register);
        listOfFunctions.put(NetCodes.CREATE_CHANNEL, ServerImpl::createChannel);
        listOfFunctions.put(NetCodes.JOIN_CHANNEL, ServerImpl::joinChannel);
        listOfFunctions.put(NetCodes.DELETE_MESSAGE, ServerImpl::deleteMessage);
        listOfFunctions.put(NetCodes.MODIFY_MESSAGE, ServerImpl::modifyMessage);
        listOfFunctions.put(NetCodes.DELETE_CHANNEL, ServerImpl::deleteChannel);
        listOfFunctions.put(NetCodes.LIST_CHANNELS_IN_SERVER, ServerImpl::listChannelsInServer);
        listOfFunctions.put(NetCodes.LIST_OF_USER_IN_CHANNEL, ServerImpl::listOfUserInChannel);
        listOfFunctions.put(NetCodes.CONSUME_MESSAGE, ServerImpl::consumeMessage);
        listOfFunctions.put(NetCodes.List_Of_MESSAGE_IN_CHANNEL, ServerImpl::listOfMessageInChannel);
        listOfFunctions.put(NetCodes.LIST_REQUEST_JOIN_CHANNEL, ServerImpl::listOfRequests);
        listOfFunctions.put(NetCodes.RESPONSE_JOIN_CHANNEL, ServerImpl::responseRequests);
        listOfFunctions.put(NetCodes.LIST_OF_JOINED_CHANNELS, ServerImpl::listOfJoinedChannels);
        listOfFunctions.put(NetCodes.LEAVE_CHANNEL, ServerImpl::leaveChannel);
        listOfFunctions.put(NetCodes.LIST_OF_UN_JOINED_CHANNELS, ServerImpl::listOfUnJoinedChannels);
        listOfFunctions.put(NetCodes.DELETE_USER_FROM_CHANNEL, ServerImpl::deleteUserFromMyChannel);
        listOfFunctions.put(NetCodes.MODIFY_CHANNEL, ServerImpl::modifyChannel);

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
