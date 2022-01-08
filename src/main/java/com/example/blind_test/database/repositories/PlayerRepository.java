package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.exception.*;
import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class PlayerRepository extends Repository {

    private static final PlayerRepository repository = new PlayerRepository();
    private static final Logger logger = LoggerFactory.getLogger(PlayerRepository.class);

    private PlayerRepository() {
    }

    public static PlayerRepository getRepository() {
        initConnectionToDatabase();
        return repository;
    }

    public Boolean verifyPlayerExistenceDB(String username, int gameId) throws PlayerAlreadyExists {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_PLAYERS_FROM_GAME);
            stmt.setInt(1, gameId);
            List<Player> players = mapper.resultSetToPlayers(stmt.executeQuery());
            if (players.contains(new Player(username, new Game.GameBuilder(gameId).build())))
                throw new PlayerAlreadyExists();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    // Please verify if return value is not null when using this method
    public Player addNewPlayerDB(String username, int gameId) throws PlayerAlreadyExists, AddNewPlayerDBException {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CREATE_PLAYER);
            if (!verifyPlayerExistenceDB(username, gameId)) {
                stmt.setString(1, username);
                stmt.setInt(2, gameId);
                stmt.executeUpdate();
                return new Player(username, new Game.GameBuilder(gameId).build());
            }
            throw new PlayerAlreadyExists();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AddNewPlayerDBException();
        }
    }

    public Boolean deleteAllPlayerDB(int gameId) throws DeleteAllPlayerDBException {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.DELETE_ALL_PLAYER_FOR_GAME);
            stmt.setInt(1, gameId);
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DeleteAllPlayerDBException();
        }
    }

    public List<Player> getPlayersOfGame(int gameId) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_PLAYERS_FROM_GAME);
            stmt.setInt(1, gameId);
            return mapper.resultSetToPlayers(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer modifyScore(int newScore, int gameID, String username) throws ModifyPlayerScoreDBException {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.MODIFY_SCORE);
            stmt.setInt(1,newScore);
            stmt.setString(2,username);
            stmt.setInt(3,gameID);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ModifyPlayerScoreDBException();
        }
    }
}
