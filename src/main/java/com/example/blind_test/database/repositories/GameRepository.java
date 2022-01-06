package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.front.models.Game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GameRepository extends Repository {
    private static final GameRepository repository = new GameRepository();

    private GameRepository() {
    }

    public static GameRepository getRepository() {
        initConnectionToDatabase();
        return repository;
    }

    public Game createGameDB(boolean type, int current_question, int rounds
            , int players, int timeQuestion, boolean state) {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CREATE_GAME,
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setBoolean(1, type);
            stmt.setInt(2, current_question);
            stmt.setInt(3, rounds);
            stmt.setInt(4, players);
            stmt.setInt(5, timeQuestion);
            stmt.setBoolean(6, state);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            int gameId = -1;
            while (rs.next()) {
                gameId = rs.getInt(1);
            }
            return new Game.GameBuilder(1).currentQuestion(gameId).type(type).currentQuestion(current_question)
                    .rounds(rounds).players(players).timeQuestion(timeQuestion).state(state).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean joinGameDB(int gameId, int idUser) {
        return false;
    }


    public List<Game> listOfNotStartedGameDb() throws SQLException {
        List<Game> games = new ArrayList<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_OF_GAME_NOT_STARTED);
            games = mapper.resultSetToGame(stmt.executeQuery());
            return games;
        } catch (SQLException e) {
            e.printStackTrace();
            return games;
        }
    }

    public Integer changeGameState(Integer gameState, Integer gameId) {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CHANGE_GAME_STATE)) {
            stmt.setInt(1, gameState);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Integer changeCurrentQuestionId(Integer currentQuestion, Integer gameId) {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CHANGE_GAME_STATE)) {
            stmt.setInt(1, currentQuestion);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
