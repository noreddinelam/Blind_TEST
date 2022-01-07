package com.example.blind_test.database;

public class SQLTablesInformation {

    public static final String PLAYER_TABLE = "PLAYER";
    public static final String PLAYER_USERNAME = "username";
    public static final String PLAYER_ID_GAME = "id_game";
    public static final String PLAYER_SCORE = "score";

    public static final String QUESTION_TABLE = "QUESTION";
    public static final String QUESTION_ID = "id";
    public static final String QUESTION_ID_RESOURCE = "id_resource";
    public static final String QUESTION_RESPONSE = "response";
    public static final String QUESTION_CHOICE1 = "choice1";
    public static final String QUESTION_CHOICE2 = "choice2";
    public static final String QUESTION_CHOICE3 = "choice3";

    public static final String QUESTION_GAME_TABLE = "QUESTION_GAME";
    public static final String QUESTION_GAME_ID_QUESTION = "id_question";
    public static final String QUESTION_GAME_ID_GAME = "id_game";
    public static final String QUESTION_GAME_ORDER = "order";
    public static final String QUESTION_GAME_STATE = "state";

    public static final String GAME_TABLE = "Game";
    public static final String GAME_ID = "id";
    public static final String GAME_TYPE = "type";
    public static final String GAME_CURRENT_QUESTION = "current_question";
    public static final String GAME_ROUNDS = "rounds";
    public static final String GAME_PLAYERS = "players";
    public static final String GAME_TIME_QUESTION = "timeQuestion";
    public static final String GAME_STATE = "state";

    private SQLTablesInformation() {
    }
}
