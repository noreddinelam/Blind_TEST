package com.example.blind_test.front.models;

public class Game {
    int id;
    int type;
    int current_qusetion;
    int rounds;
    int players;
    int timeQuestion;
    int state;
    public Game(int id, int type, int currentQuestion, int rounds, int players, int timeQUESTION, int state){
        this.id=id;
        this.type=type;
        this.current_qusetion=currentQuestion;
        this.rounds=rounds;
        this.players=players;
        this.timeQuestion= timeQUESTION;
        this.state=state;
    }
}
