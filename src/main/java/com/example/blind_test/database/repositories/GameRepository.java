package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.exception.GameAlreadyExists;
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


    private Boolean verifyGameExistsDB(int id) {
        List<Game> games = new ArrayList<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_ALL_GAMES);
            games = Mapper.getMapper().resultSetToGame(stmt.executeQuery());
            for (Game game : games) {
                if (game.getId() == id) return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public Game createGameDB(int id, Boolean type, int current_question, int rounds
            , int players, int timeQuestion, Boolean state) {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_OF_GAME_NOT_STARTED);
            if (!verifyGameExistsDB(id)) {
                stmt.setInt(1, id);
                stmt.setBoolean(2, type);
                stmt.setInt(3, current_question);
                stmt.setInt(4, rounds);
                stmt.setInt(5, players);
                stmt.setInt(6, timeQuestion);
                stmt.setBoolean(7, state);
                if (stmt.execute()) {
                    return new Game(id, type, current_question, rounds, players, timeQuestion, state);
                }
            }
            throw new GameAlreadyExists();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Boolean joinGameDB(int gameId,int idUser)
    {

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
