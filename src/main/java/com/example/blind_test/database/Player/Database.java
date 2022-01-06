package com.example.blind_test.database.Player;

import com.example.blind_test.shared.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {
    private static Connection database;

    private Database() {
    }

    public static Connection getDatabaseConnection() throws SQLException {
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("user", Properties.DATABASE_USER);
        properties.setProperty("password", Properties.DATABASE_PASSWORD);
        properties.setProperty("useSSL", Properties.DATABASE_USE_SSL);
        properties.setProperty("serverTimezone",Properties.SERVER_TIME_ZONE);
        if (database == null)
            database = DriverManager.getConnection(Properties.DATABASE_URL,
                    properties);
        return database;
    }
}
