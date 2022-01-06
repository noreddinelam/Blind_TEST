package com.example.blind_test.database;

import static com.example.blind_test.database.SQLTablesInformation.*;

public class SQLStatements {

    public static String CREATE_PLAYER =
            "INSERT INTO " + PLAYER_TABLE
                    + "(" + PLAYER_USERNAME_COLUMN + "," + PLAYER_ID_GAME_COLUMN + "," + PLAYER_SCORE_COLUMN
                    + ")" +
                    " VALUES (?,?,0) ;";

    public static String modifyPlayerScore =
            "UPDATE " + PLAYER_TABLE
                    + " SET " + PLAYER_SCORE_COLUMN + "=? , WHERE " + PLAYER_USERNAME_COLUMN + "=? AND " + PLAYER_ID_GAME_COLUMN + "=? ;";

    public static String deleteAllPlayer =
            "DELETE FROM " + PLAYER_TABLE
                    + " WHERE " + PLAYER_ID_GAME_COLUMN + "=? ;";

    public static final String LIST_PLAYERS_FROM_GAME = "SELECT * FROM " + PLAYER_TABLE + " where " + PLAYER_ID_GAME_COLUMN + " = ? ;";


    public static final String GET_RESPONSE_FOR_QUESTION =
            "SELECT " + QUESTION_RESPONSE + " FROM " + QUESTION_TABLE + " WHERE " + QUESTION_ID + "= ? ;";

    public static final String GET_QUESTION =
            "SELECT * FROM " + QUESTION_TABLE + " WHERE " + QUESTION_ID + "= ? ;";

    public static final String INSERT_QUESTION_IN_QUESTION_GAME = "INSERT INTO " + QUESTION_GAME_TABLE + " VALUES (?," +
            "?,?) ;";

    public static final String CREATE_GAME = "INSERT INTO " + GAME_TABLE
            + "(" + GAME_ID + "," + GAME_TYPE + ","
            + GAME_CURRENT_QUESTION + "," + GAME_ROUNDS + "," + GAME_PLAYERS
            + "," + GAME_TIME_QUESTION + "," + GAME_STATE + ")" +
            " VALUES (?,?,?,?,?,?,?) ;";

    public static final String LIST_ALL_GAMES = "SELECT * FROM " + GAME_TABLE + " ;";

    public static final String LIST_OF_GAME_NOT_STARTED = "SELECT * FROM"
            + GAME_TABLE + " WHERE " + GAME_STATE + "=0 ;";

    public static final String CHANGE_GAME_STATE = "UPDATE " + GAME_TABLE + " SET " + GAME_STATE + "= ?" +
            " WHERE " + GAME_ID + "= ?; ";

    public static final String ID_OF_CURRENT_QUESTION = "UPDATE " + GAME_TABLE + " SET "
            + GAME_CURRENT_QUESTION + " = ? " +
            " WHERE " + GAME_ID + "= ?; ";

    private SQLStatements() {
    }


}

