package com.example.blind_test.database.repositories;

import com.example.blind_test.database.Database;
import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.exception.PlayerAlreadyExists;
import com.example.blind_test.front.models.Player;
import com.example.blind_test.shared.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PlayerRepository {

    private static final PlayerRepository repository = new PlayerRepository();
    private static final Logger logger = LoggerFactory.getLogger(PlayerRepository.class);
    private static Connection connectionDB;

    private PlayerRepository() {
    }

    public static PlayerRepository getRepository() {
        initConnectionToDatabase();
        return repository;
    }

    private static void initConnectionToDatabase() {
        try {
            connectionDB = Database.getDatabaseConnection();
            logger.info("Initialisation of connection to database");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error on getting connection from DB");
        }
    }

    private Boolean verifyPlayerExistenceDB(String playerId,int gameId) {
        List<Player> players = new ArrayList<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_PLAYERS_FROM_GAME);
            stmt.setInt(1,gameId);
            players = Mapper.getMapper().resultSetToPlayers(stmt.executeQuery());
            for(Player player:players)
            {
                if(player.getGameId() == gameId && player.getUsername().equalsIgnoreCase(playerId)) throw new PlayerAlreadyExists();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    // Please verify if return value is not null when using this method
    public Player addNewPlayerDB(String username,int gameId) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.CREATE_PLAYER);
            if(!verifyPlayerExistenceDB(username,gameId))
            {
                stmt.setString(1,username);
                stmt.setInt(2,gameId);
                stmt.setInt(3,0);
                return new Player(username,gameId);
            }
            throw new PlayerAlreadyExists();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Boolean> modifyPlayerScoreDB(int newScore) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.modifyPlayerScore);
            stmt.setInt(1, newScore);
            return Optional.of(stmt.execute());
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Boolean> deleteAllPlayerDB(String gameId) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.deleteAllPlayer);
            stmt.setString(1,gameId);
            return Optional.of(stmt.execute());
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
