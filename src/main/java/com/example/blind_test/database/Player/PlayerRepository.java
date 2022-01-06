package com.example.blind_test.database.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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

    public Optional<Integer> addNewPlayerDB(String username,String gameId) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLPlayerStatements.addNewPlayer);
            stmt.setString(1,username);
            stmt.setString(2,gameId);
            return Optional.of(stmt.executeUpdate());
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public Optional<Boolean> modifyPlayerScoreDB(int newScore) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLPlayerStatements.modifyPlayerScore);
            stmt.setInt(1, newScore);
            return Optional.of(stmt.execute());
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Boolean> deleteAllPlayerDB(String gameId) {
        try  {
            PreparedStatement stmt = connectionDB.prepareStatement(SQLPlayerStatements.deleteAllPlayer);
            stmt.setString(1,gameId);
            return Optional.of(stmt.execute());
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
