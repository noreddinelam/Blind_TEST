package com.example.blind_test.database.repositories;

import com.example.blind_test.database.SQLStatements;
import com.example.blind_test.exception.AddNewPlayerDBException;
import com.example.blind_test.exception.DeleteAllPlayerDBException;
import com.example.blind_test.exception.GetPlayersOfGameException;
import com.example.blind_test.exception.ModifyPlayerScoreDBException;
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

    public Boolean modifyPlayerScoreDB(int newScore) throws ModifyPlayerScoreDBException {
        try (PreparedStatement stmt = connectionDB.prepareStatement(SQLStatements.MODIFY_NEW_PLAYER)) {
            stmt.setInt(1, newScore);
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ModifyPlayerScoreDBException();
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
            return mapper.resultSetToPlayerList(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GetPlayersOfGameException();
        }
    }


}
