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
import com.example.blind_test.shared.communication.*;
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
            Response broadcastResponse = new Response(NetCodes.CREATE_GAME_BROADCAST_SUCCEED,
                    GsonConfiguration.gson.toJson(player.getGame()));
            listOfGuests.entrySet().stream().forEach((entry) -> responseBroadcast(broadcastResponse, entry.getValue()));
        } catch (CreateGameDBException e) {
            Response response = new Response(NetCodes.CREATE_GAME_FAILED, "Create game failure");
            response(response, client);
        }
    }

    private static void deleteGame(String data) throws GetPlayersOfGameException {
        logger.info("CREATE GAME INFO {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAME_ID));
        List<Player> list = playerRepository.getPlayersOfGame(gameId);
        try {
            gameRepository.deleteGameDB(gameId);
            Response response = new Response(NetCodes.DELETE_GAME_SUCCEED, "Game deleted!");
            for (Player playerOther : list) {
                responseBroadcast(response, listOfPlayers.get(new Credentials(playerOther.getUsername(), gameId)));
            }
            for (Map.Entry<Credentials, AsynchronousSocketChannel> entryCredential : listOfPlayers.entrySet()) {
                if (entryCredential.getKey().getGameId() == gameId) {
                    addGuestClients(entryCredential.getValue());
                    listOfPlayers.remove(entryCredential.getKey());
                }
            }
            //listOfPlayers.entrySet().removeIf((entry) -> entry.getKey().getGameId() == gameId);
        } catch (DeleteGameException e) {
            Response response = new Response(NetCodes.DELETE_GAME_FAILED, "delete game failure");
            for (Player playerOther : list) {
                responseBroadcast(response, listOfPlayers.get(new Credentials(playerOther.getUsername(), gameId)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void joinGame(String data) {
        logger.info("JOIN GAME INFO {} ", data);
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        String ipAddress = requestData.get(FieldsRequestName.IP_ADDRESS);
        AsynchronousSocketChannel clientJoin = listOfGuests.get(ipAddress);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        try {
            List<Player> list = playerRepository.getPlayersOfGame(gameId);
            Player player = gameRepository.joinGameDB(gameId, username);
            JoinGameType joinGameType = new JoinGameType(player,list);
            Response response = new Response(NetCodes.JOIN_GAME_SUCCEED, GsonConfiguration.gson.toJson(joinGameType));
            listOfGuests.remove(ipAddress);
            response(response, clientJoin);
            Response aPlayerHasJoined = new Response(NetCodes.JOIN_GAME_BROADCAST_SUCCEED,
                    GsonConfiguration.gson.toJson(player));
            for (Player playerOther : list) {
                responseBroadcast(aPlayerHasJoined, listOfPlayers.get(new Credentials(playerOther.getUsername(), gameId)));
            }
            listOfPlayers.put(new Credentials(username, player.getGame().getId()), clientJoin);
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
            responseData.put(FieldsRequestName.LIST_GAMES, resultGame);
            Response response = new Response(NetCodes.LIST_OF_GAME_NOT_STARTED_SUCCEED,
                    GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapListGameJsonTypeData));
            response(response, client);
        } catch (ListOfNotStartedGameException e) {
            Response response = new Response(NetCodes.LIST_OF_GAME_NOT_STARTED_FAILED, "list of not started game " +
                    "failure");
            response(response, client);
        }
    }

    public static void startGame(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        boolean type = Boolean.parseBoolean((requestData.get(FieldsRequestName.GAME_TYPE)));
        int rounds = Integer.parseInt(requestData.get(FieldsRequestName.ROUNDS));
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {
            gameRepository.changeGameState(gameId);
            List<Question> questions = questionRepository.fetchQuestion(type);
            List<Player> players = playerRepository.getPlayersOfGame(gameId);
            int random = new Random().nextInt(rounds);
            for (int i = 0; i < random; i++)
                Collections.shuffle(questions);
            Question firstQuestion = questions.get(0);
            int j = 0;
            for (Question q : questions) {
                questionRepository.generateQuestion(q.getQuestionId(), gameId, j);
                j++;
                if (j == rounds) break;
            }
            Response response = new Response(NetCodes.START_GAME_SUCCEED, GsonConfiguration.gson.toJson(firstQuestion));
            for (Player playerOther : players) {
                responseBroadcast(response, listOfPlayers.get(new Credentials(playerOther.getUsername(), gameId)));
            }
            ByteBuffer newBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
            client.read(newBuffer,newBuffer,new ServerReaderCompletionHandler());
        } catch (FetchQuestionException e) {
            e.printStackTrace();
        } catch (GenerateQuestionException | ChangeGameStateException | GetPlayersOfGameException e) {
            e.printStackTrace();
        }
    }

    public static void modifyPlayerScore(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        int score = Integer.parseInt(requestData.get(FieldsRequestName.PLAYER_SCORE));
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
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAME_ID));
        String username = requestData.get(FieldsRequestName.USERNAME);
        int idCurrentQuestion = Integer.parseInt(requestData.get(FieldsRequestName.CURRENT_QUESTION));
        String playerResponse = requestData.get(FieldsRequestName.PLAYER_RESPONSE);
        int score = Integer.parseInt(requestData.get(FieldsRequestName.PLAYER_SCORE));
        AsynchronousSocketChannel client = listOfPlayers.get(new Credentials(username, gameId));
        try {
            if (questionRepository.verifyQuestionState(idCurrentQuestion) == 0) {
                Question question = QuestionRepository.getRepository().getQuestion(idCurrentQuestion);
                String questionResponse = question.getResponse();
                Map<String, String> responseData = new HashMap<>();
                responseData.put(FieldsRequestName.GAME_ID, String.valueOf(gameId));
                responseData.put(FieldsRequestName.USERNAME, username);
                responseData.put(FieldsRequestName.CURRENT_QUESTION, questionResponse);

                if (questionResponse.equalsIgnoreCase(playerResponse)) {
                    score = score + 1;
                    int i = playerRepository.modifyScore(score, gameId, username);
                    questionRepository.changeQuestionState(idCurrentQuestion);
                    responseData.put(FieldsRequestName.STATE, "true");
                } else {
                    responseData.put(FieldsRequestName.STATE, "false");
                }
                Response response = new Response(NetCodes.GET_RESPONSE_FOR_QUESTION_SUCCEED,
                        GsonConfiguration.gson.toJson(responseData, CommunicationTypes.mapJsonTypeData));
                response(response, client);
            } else {
                Response response = new Response(NetCodes.START_GAME_FAILED, "the question responded");
                response(response, client);
            }

        } catch (Exception e) {
            Response response = new Response(NetCodes.GET_RESPONSE_FOR_QUESTION_FAILED, "get response for question " +
                    "failure");
            response(response, client);
        }
    }

    public static void nextRoundInformation(String data) {
        Map<String, String> requestData = GsonConfiguration.gson.fromJson(data, CommunicationTypes.mapJsonTypeData);
        int gameId = Integer.parseInt(requestData.get(FieldsRequestName.GAME_ID));
        int questionOrder = Integer.parseInt(requestData.get(FieldsRequestName.CURRENT_QUESTION));
        try {
            List<Player> list = playerRepository.getPlayersOfGame(gameId);
            Question nextQuestion = questionRepository.getQuestionByOrder(gameId, questionOrder);
            NextRoundInformation nextRoundInformation = new NextRoundInformation(list, nextQuestion);
            Response response = new Response(NetCodes.NEXT_ROUND_SUCCEEDED,
                    GsonConfiguration.gson.toJson(nextRoundInformation));
            for (Player player : list) {
                responseBroadcast(response, listOfPlayers.get(new Credentials(player.getUsername(), gameId)));
            }
        } catch (GetPlayersOfGameException e) {
            e.printStackTrace();
        }
    }

    public static void initListOfFunctions() {
        // initialisation of methods;
        listOfFunctions.put(NetCodes.LIST_OF_GAME_NOT_STARTED, ServerImpl::listOfNotStartedGame);
        listOfFunctions.put(NetCodes.START_GAME, ServerImpl::startGame);
        listOfFunctions.put(NetCodes.MODIFY_SCORE, ServerImpl::modifyPlayerScore);
        listOfFunctions.put(NetCodes.CREATE_GAME, ServerImpl::createGame);
        listOfFunctions.put(NetCodes.GET_RESPONSE_FOR_QUESTION, ServerImpl::getQuestionResponse);
        listOfFunctions.put(NetCodes.JOIN_GAME, ServerImpl::joinGame);
        listOfFunctions.put(NetCodes.NEXT_ROUND, ServerImpl::nextRoundInformation);
    }

    public static Consumer<String> getFunctionWithRequestCode(Request request) {
        return listOfFunctions.get(request.getNetCode());
    }

    public static void addGuestClients(AsynchronousSocketChannel client) throws IOException {
        listOfGuests.put(client.getRemoteAddress().toString().split(":")[1], client);
    }

    private static void responseBroadcast(Response response, AsynchronousSocketChannel client) {
        String responseJson = GsonConfiguration.gson.toJson(response);
        ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
        client.write(attachment, attachment, new ServerWriterCompletionHandler());
        attachment.clear();
    }

    private static void response(Response response, AsynchronousSocketChannel client) {
        String responseJson = GsonConfiguration.gson.toJson(response);
        ByteBuffer attachment = ByteBuffer.wrap(responseJson.getBytes());
        client.write(attachment, attachment, new ServerWriterCompletionHandler());
        attachment.clear();
        ByteBuffer newByteBuffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
        client.read(newByteBuffer, newByteBuffer, new ServerReaderCompletionHandler());
    }

}
