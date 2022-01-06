package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.exception.*;
import com.example.blind_test.front.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public Integer addNewPlayerDB(String username, String gameId) throws AddNewPlayerDBException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.ADD_NEW_PLAYER)) {
            stmt.setString(1, username);
            stmt.setString(2, gameId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AddNewPlayerDBException();
        }
    }

    private Boolean verifyPlayerExistenceDB(String playerId,int gameId) {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.LIST_PLAYERS_FROM_GAME);
            stmt.setInt(1,gameId);
            List<Player> players = mapper.resultSetToPlayers(stmt.executeQuery());
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
    public Player addNewPlayerDB(String username,int gameId) throws PlayerAlreadyExists{
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

    public List<Player> getPlayersOfGame(int gameId) throws GetPlayersOfGameException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.DELETE_ALL_PLAYER_FOR_GAME)) {
            stmt.setInt(1, gameId);
            return mapper.resultSetToPlayers(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GetPlayersOfGameException();
        }
    }


}
