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

}
