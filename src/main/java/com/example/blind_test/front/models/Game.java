package com.example.blind_test.front.models;

public class Game {
    int id;
    Boolean type;
    int current_qusetion;
    int rounds;
    int players;
    int timeQuestion;
    Boolean state;
    public Game(int id, Boolean type, int currentQuestion, int rounds, int players, int timeQUESTION, Boolean state){
        this.id=id;
        this.type=type;
        this.current_qusetion=currentQuestion;
        this.rounds=rounds;
        this.players=players;
        this.timeQuestion= timeQUESTION;
        this.state=state;
    }

    public int getId() {
        return id;
    }
}
