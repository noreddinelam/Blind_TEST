package com.example.blind_test.shared;

import com.example.blind_test.database.SQLTablesInformation;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Question;
import com.example.blind_test.front.models.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Mapper {
    private static final Mapper mapper = new Mapper();
    private static final Random random = new Random();

    private Mapper() {
    }

    public static Mapper getMapper() {
        return mapper;
    }

    public List<Player> resultSetToPlayers(ResultSet rs) throws SQLException {
        List<Player> players = new ArrayList<>();
        String username;
        int gameId;
        int score;
        while(rs.next())
        {
            username=rs.getString(SQLTablesInformation.PLAYER_USERNAME_COLUMN);
            gameId=rs.getInt(SQLTablesInformation.PLAYER_ID_GAME);
            score=rs.getInt(SQLTablesInformation.PLAYER_SCORE);
            players.add(new Player(username,gameId,score));
        }
        return players;
    }

    public List<Game> resultSetToGame(ResultSet resultSet) throws SQLException {
        List<Game> games = new ArrayList<>();
        int id;
        Boolean type;
        int current_qusetion;
        int rounds;
        int players;
        int timeQuestion;
        Boolean state;
        while (resultSet.next()) {
            id = resultSet.getInt(SQLTablesInformation.GAME_ID);
            type = resultSet.getBoolean(SQLTablesInformation.GAME_TYPE);
            current_qusetion = resultSet.getInt(SQLTablesInformation.GAME_CURRENT_QUESTION);
            rounds = resultSet.getInt(SQLTablesInformation.GAME_ROUNDS);
            players = resultSet.getInt(SQLTablesInformation.GAME_PLAYERS);
            timeQuestion = resultSet.getInt(SQLTablesInformation.GAME_TIME_QUESTION);
            state = resultSet.getBoolean(SQLTablesInformation.GAME_STATE);
            games.add(new Game(id, type, current_qusetion, rounds, players, timeQuestion, state));
        }
        return games;
    }

    public String resultSetToQuestionResponse(ResultSet resultSet) throws SQLException {
        String response = "";
        while (resultSet.next()) {
            response = resultSet.getString(SQLTablesInformation.QUESTION_RESPONSE);
        }
        return response;
    }

    public Question resultSetToQuestion(ResultSet resultSet) throws SQLException {
        int questionId = -1;
        String resource = "";
        String response = "";
        List<String> list = new ArrayList<>();
        int randomInt = random.nextInt();
        while (resultSet.next()) {
            questionId = resultSet.getInt(SQLTablesInformation.QUESTION_ID);
            resource = resultSet.getString(SQLTablesInformation.QUESTION_ID_RESOURCE);
            response = resultSet.getString(SQLTablesInformation.QUESTION_RESPONSE);
            list.add(resultSet.getString(SQLTablesInformation.QUESTION_CHOICE1));
            list.add(resultSet.getString(SQLTablesInformation.QUESTION_CHOICE2));
            list.add(resultSet.getString(SQLTablesInformation.QUESTION_CHOICE3));
            list.add(response);
        }
        for (int i = 0; i < randomInt; i++)
            Collections.shuffle(list);
        return new Question.QuestionBuilder(questionId).resource(resource).response(response).choices(list).build();
    }

}
