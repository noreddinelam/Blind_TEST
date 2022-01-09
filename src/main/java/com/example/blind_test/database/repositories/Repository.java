package com.example.blind_test.database.repositories;

import com.example.blind_test.database.Database;
import com.example.blind_test.shared.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Repository {
    protected static Connection connectionDB;
    protected static Mapper mapper = Mapper.getMapper();
    private static Logger logger = LoggerFactory.getLogger(Repository.class);

    public static void initConnectionToDatabase() {
        try {
            connectionDB = Database.getDatabaseConnection();
            logger.info("Initialisation of connection to database");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error on getting connection from DB");
        }
    }

}
