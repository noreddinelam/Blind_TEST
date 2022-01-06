package com.example.blind_test.database.Player;

import static com.example.blind_test.database.Player.SQLPlayerTableInformation.*;

public class SQLPlayerStatements {

    public static String addNewPlayer =
            "INSERT INTO " + PLAYER_TABLE
                    + "(" + PLAYER_USERNAME_COLUMN + "," + PLAYER_ID_GAME_COLUMN + "," + PLAYER_SCORE_COLUMN
                    + ")" +
                    " VALUES (?,?,0) ;";

    public static String modifyPlayerScore =
            "UPDATE " + PLAYER_TABLE
                    + " SET " + PLAYER_SCORE_COLUMN + "=? , WHERE " + PLAYER_USERNAME_COLUMN + "=? AND " + PLAYER_ID_GAME_COLUMN + "=? ;";

    public static String deleteAllPlayer =
            "DELETE FROM " + PLAYER_TABLE
                    + " WHERE " + PLAYER_ID_GAME_COLUMN+ "=? ;";

    private SQLPlayerStatements() {
    }
}

