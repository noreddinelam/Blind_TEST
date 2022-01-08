package com.example.blind_test.shared;

public class NetCodes {

    public static final String CREATE_GAME = "0";
    public static final String CREATE_GAME_SUCCEED = "1";
    public static final String CREATE_GAME_FAILED = "2";
    public static final String CREATE_GAME_BROADCAST_SUCCEED = "3";
    public static final String CREATE_GAME_BROADCAST_FAILED = "4";

    public static final String JOIN_GAME = "100";
    public static final String JOIN_GAME_SUCCEED = "101";
    public static final String JOIN_GAME_FAILED = "102";
    public static final String JOIN_GAME_BROADCAST_SUCCEED = "103";
    public static final String JOIN_GAME_BROADCAST_FAILED = "104";

    public static final String LEAVE_GAME = "105";
    public static final String LEAVE_GAME_SUCCEED = "106";
    public static final String LEAVE_GAME_FAILED = "107";
    public static final String LEAVE_GAME_BROADCAST = "108";

    public static final String DELETE_GAME = "200";
    public static final String DELETE_GAME_BROADCAST_SUCCEED = "201";
    public static final String DELETE_GAME_FAILED = "202";


    public static final String LIST_OF_GAME_NOT_STARTED = "300";
    public static final String LIST_OF_GAME_NOT_STARTED_SUCCEED = "301";
    public static final String LIST_OF_GAME_NOT_STARTED_FAILED = "302";

    public static final String START_GAME = "400";
    public static final String START_GAME_SUCCEED = "401";
    public static final String START_GAME_FAILED = "402";


    public static String ID_OF_CURRENT_QUESTION = "500";
    public static String ID_OF_CURRENT_QUESTION_SUCCEED = "501";
    public static String ID_OF_CURRENT_QUESTION_FAILED = "502";

    public static String GET_RESPONSE_FOR_QUESTION = "600";
    public static String GET_RESPONSE_FOR_QUESTION_SUCCEED = "601";
    public static String GET_RESPONSE_FOR_QUESTION_FAILED = "602";

    public static String GET_QUESTION = "700";
    public static String GET_QUESTION_SUCCEED = "701";
    public static String GET_QUESTION_FAILED = "702";

    public static String INSERT_QUESTION_IN_QUESTION_GAME = "800";
    public static String INSERT_QUESTION_IN_QUESTION_GAME_SUCCEED = "801";
    public static String INSERT_QUESTION_IN_QUESTION_GAME_FAILED = "802";

    public static String CREATE_PLAYER = "900";
    public static String CREATE_PLAYER_SUCCEED = "901";
    public static String CREATE_PLAYER_FAILED = "902";

    public static String MODIFY_SCORE = "1000";
    public static String MODIFY_SCORE_SUCCEED = "1001";
    public static String MODIFY_SCORE_FAILED = "1002";

    public static String DELETE_PLAYER = "1100";
    public static String DELETE_PLAYER_SUCCEED = "1101";
    public static String DELETE_PLAYER_FAILED = "1102";


    public static String CHANGE_QUESTION_STATE = "1200";
    public static String CHANGE_QUESTION_STATE_SUCCEED = "1201";
    public static String CHANGE_QUESTION_STATE_FAILED = "1202";
    public static String CHANGE_QUESTION_STATE_BROADCAST_SUCCEED = "1203";
    public static String CHANGE_QUESTION_STATE_BROADCAST_FAILED = "1204";


    public static final String NEXT_ROUND = "1300";
    public static final String NEXT_ROUND_SUCCEEDED = "1301";
    public static final String NEXT_ROUND_FAILED = "1302";



    private NetCodes() {
    }
}
