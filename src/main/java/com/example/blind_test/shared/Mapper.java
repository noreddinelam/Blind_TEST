package com.example.blind_test.shared;

import com.example.blind_test.database.SQLTablesInformation;
import com.example.blind_test.front.models.Game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mapper {
    private static final Mapper mapper = new Mapper();

    private Mapper() {
    }

    public static Mapper getMapper() {
        return mapper;
    }

    public List<Game> resultSetToGame(ResultSet resultSet) throws SQLException {
        List<Game> games = new ArrayList<>();
        int id;
        int type;
        int current_question;
        int rounds;
        int players;
        int timeQuestion;
        int state;
        while (resultSet.next()) {
            id = resultSet.getInt(SQLTablesInformation.GAME_ID);
            type = resultSet.getInt(SQLTablesInformation.GAME_TYPE);
            current_question = resultSet.getInt(SQLTablesInformation.GAME_CURRENT_QUESTION);
            rounds = resultSet.getInt(SQLTablesInformation.GAME_ROUNDS);
            players = resultSet.getInt(SQLTablesInformation.GAME_PLAYERS);
            timeQuestion = resultSet.getInt(SQLTablesInformation.GAME_TIME_QUESTION);
            state = resultSet.getInt(SQLTablesInformation.GAME_STATE);
            games.add(new Game(id, type, current_question, rounds, players, timeQuestion, state));
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

    public List<String> resultSetToQuestionChoicesList(ResultSet resultSet) throws SQLException {
        String choice1 = "";
        String choice2 = "";
        String choice3 = "";
        String choice4 = "";
        List<String> list = new ArrayList<>();
        while (resultSet.next()) {
            choice1 = resultSet.getString(SQLTablesInformation.QUESTION_CHOICE1);
            choice2 = resultSet.getString(SQLTablesInformation.QUESTION_CHOICE2);
            choice3 = resultSet.getString(SQLTablesInformation.QUESTION_CHOICE3);
            choice4 = resultSet.getString(SQLTablesInformation.QUESTION_RESPONSE);
        }
        list.add(choice1);
        list.add(choice2);
        list.add(choice3);
        list.add(choice4);
        return list;
    }

}
