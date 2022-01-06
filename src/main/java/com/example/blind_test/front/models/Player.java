package com.example.blind_test.front.models;

public class Player {
    private String username;
    private int gameId;
    private int score;

    public Player(String username,int gameId){
        this.username = username;
        this.gameId = gameId;
        this.score = 0;
    }

    public Player(String username,int gameId,int score){
        this.username = username;
        this.gameId = gameId;
        this.score = score;
    }
}
