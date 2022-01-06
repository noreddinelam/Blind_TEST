package com.example.blind_test.database;

import static com.example.blind_test.database.SQLTablesInformation.*;

public class SQLStatements {

    public static final String GET_RESPONSE_FOR_QUESTION =
            "SELECT " + QUESTION_RESPONSE + " FROM " + QUESTION_TABLE + " WHERE " + QUESTION_ID + "= ? ;";

    public static final String GET_CHOICES_FOR_QUESTION =
            "SELECT * FROM " + QUESTION_TABLE + " WHERE " + QUESTION_ID + "= ? ;";

    public static final String INSERT_QUESTION_IN_QUESTION_GAME = "INSERT INTO " + QUESTION_GAME_TABLE + " VALUES (?," +
            "?,?) ;";

    private SQLStatements() {
    }
}

