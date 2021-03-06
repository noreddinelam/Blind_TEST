package com.example.blind_test.database;

import com.example.blind_test.exception.GameAlreadyExists;

import static com.example.blind_test.database.SQLTablesInformation.*;

public class SQLStatements {

    public static final String LIST_PLAYERS_FROM_GAME =
            "SELECT * FROM " + PLAYER_TABLE + " where " + PLAYER_ID_GAME + " = ? ;";
    public static final String DEC_PLAYERS_IN_GAME = "UPDATE " + GAME_TABLE +
            " SET " + GAME_PLAYERS + " = " + GAME_PLAYERS + " - 1 " +
            "WHERE " + GAME_ID + " = ? ; ";
    public static final String INC_PLAYERS_IN_GAME = "UPDATE " + GAME_TABLE +
            " SET " + GAME_PLAYERS + " = " + GAME_PLAYERS + " + 1 " +
            "WHERE " + GAME_ID + " = ? ; ";
    public static final String GET_PLAYERS_FROM_GAME = "SELECT " + GAME_PLAYERS + " FROM " + GAME_TABLE + " WHERE id=" +
            " ? ;";
    public static final String GET_GAME_FROM_ID = "SELECT * FROM " + GAME_TABLE + " WHERE id= ? ;";
    public static final String GET_RESPONSE_FOR_QUESTION =
            "SELECT " + QUESTION_RESPONSE + " FROM " + QUESTION_TABLE + " WHERE " + QUESTION_ID + "= ? ;";
    public static final String GET_QUESTION =
            "SELECT * FROM " + QUESTION_TABLE + " WHERE " + QUESTION_ID + "= ? ;";

    // QUESTIONS
    public static final String INSERT_QUESTION_IN_QUESTION_GAME =
            "INSERT INTO " + QUESTION_GAME_TABLE + "(" + QUESTION_GAME_ID_QUESTION + "," + QUESTION_GAME_ID_GAME + ","
                    + QUESTION_GAME_ORDER + ") VALUES (?,?,?) ;";

    public static final String GET_QUESTION_BY_ORDER =
            "SELECT * FROM " + QUESTION_GAME_TABLE + " INNER JOIN " + QUESTION_TABLE + " ON " + QUESTION_GAME_TABLE
                    + "." + QUESTION_GAME_ID_QUESTION + " = " + QUESTION_TABLE + "." + QUESTION_ID
                    + " WHERE " + QUESTION_GAME_ID_GAME + "= ? AND " + QUESTION_GAME_ORDER + "= ? ;";

    //GAME
    public static final String CREATE_GAME = "INSERT INTO " + GAME_TABLE
            + "(" + GAME_TYPE + "," + GAME_ROUNDS + "," + GAME_PLAYERS + "," + GAME_TOTAL_PLAYERS
            + "," + GAME_TIME_QUESTION + "," + GAME_STATE + ")" +
            " VALUES (?,?,?,?,?,?) ;";

    public static final String DELETE_GAME = "DELETE FROM " + GAME_TABLE + " where " + GAME_ID + " = ? ;";

    public static final String LIST_OF_GAME_NOT_STARTED = "SELECT * FROM "
            + GAME_TABLE + " WHERE " + GAME_STATE + "= 0 ;";

    public static final String CHANGE_GAME_STATE = "UPDATE " + GAME_TABLE + " SET " + GAME_STATE + "= 1" +
            " WHERE " + GAME_ID + "= ?; ";

    //PLAYER
    public static final String MODIFY_SCORE =
            "UPDATE " + PLAYER_TABLE
                    + " SET " + PLAYER_SCORE + "=?  WHERE " + PLAYER_USERNAME + "=? AND " + PLAYER_ID_GAME + "=? ;";
    public static final String DELETE_ALL_PLAYER_FOR_GAME =
            "DELETE FROM " + PLAYER_TABLE
                    + " WHERE " + PLAYER_ID_GAME + "=? ;";
    public static String CREATE_PLAYER =
            "INSERT INTO " + PLAYER_TABLE
                    + " (" + PLAYER_USERNAME + "," + PLAYER_ID_GAME + "," + PLAYER_SCORE
                    + ")" +
                    " VALUES (?,?,0) ;";
    public static String DELETE_PLAYER_FROM_GAME = "DELETE FROM " + PLAYER_TABLE + " WHERE " + PLAYER_ID_GAME
            +" = ? and " + PLAYER_USERNAME + " = ? ;";

    public static final String CHANGE_QUESTION_STATE = "UPDATE "+ QUESTION_GAME_TABLE + " SET "+QUESTION_GAME_STATE +
            " = 1 WHERE " + QUESTION_GAME_ORDER + " = ? AND " + QUESTION_GAME_ID_GAME +" =?;";

    public static final String GENERATE_QUESTION = " INSERT INTO "+ QUESTION_GAME_TABLE +
            " ( " + QUESTION_GAME_ID_QUESTION + "," + QUESTION_GAME_ID_GAME+ "," + QUESTION_GAME_ORDER + ", "+ QUESTION_GAME_STATE +")"+
            "VALUE ( ?,?,?,0);";

    public static final String FETCH_QUESTION_DEPENDING_ON_TYPE = "SELECT * FROM " + QUESTION_TABLE + " WHERE " + QUESTION_type +
            "=?;";



    public static final String VERIFY_QUESTION_STATE = "SELECT "+  QUESTION_GAME_STATE + " FROM "+ QUESTION_GAME_TABLE +
            "  WHERE " + QUESTION_GAME_ORDER + " = ? AND " + QUESTION_GAME_ID_GAME +" =?;";
    private SQLStatements() {
    }


}

