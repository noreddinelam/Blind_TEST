package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.shared.Mapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
