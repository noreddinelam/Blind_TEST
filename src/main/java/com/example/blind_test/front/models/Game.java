package com.example.blind_test.front.models;

public class Game {
    private int id;
    private boolean type;
    private int current_question;
    private int rounds;
    private int players;
    private int timeQuestion;
    private boolean state;
    private Game(GameBuilder gb){
        this.id = gb.id;
        this.type = gb.type;
        this.current_question = gb.currentQuestion;
        this.rounds = gb.rounds;
        this.players = gb.players;
        this.timeQuestion = gb.timeQuestion;
        this.state = gb.state;
    }

    public static class GameBuilder{
        private int id;
        private boolean type;
        private int currentQuestion;
        private int rounds;
        private int players;
        private int timeQuestion;
        private boolean state;
        public GameBuilder(int gameId){
            this.id = gameId;
        }

        public GameBuilder type(boolean type){
            this.type = type;
            return this;
        }

        public GameBuilder currentQuestion(int currentQuestion){
            this.currentQuestion = currentQuestion;
            return this;
        }

        public GameBuilder rounds(int rounds){
            this.rounds = rounds;
            return this;
        }

        public GameBuilder players(int players){
            this.players = players;
            return this;
        }

        public GameBuilder timeQuestion(int timeQuestion){
            this.timeQuestion = timeQuestion;
            return this;
        }

        public GameBuilder state(boolean state){
            this.state = state;
            return this;
        }

        public Game build(){
            return new Game(this);
        }
    }

    public int getId() {
        return id;
    }
}
