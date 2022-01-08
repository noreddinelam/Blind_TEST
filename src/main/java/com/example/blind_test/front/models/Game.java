package com.example.blind_test.front.models;

import java.util.List;

public class Game {
    private int id;
    private boolean type;
    private int rounds;
    private int remainedPlayers;
    private int totalPlayers;
    private int timeQuestion;
    private boolean state;
    private List<Question> questions;

    private Game(GameBuilder gb) {
        this.id = gb.id;
        this.type = gb.type;
        this.rounds = gb.rounds;
        this.remainedPlayers = gb.remainedPlayers;
        this.totalPlayers = gb.totalPlayers;
        this.timeQuestion = gb.timeQuestion;
        this.state = gb.state;
    }

    public int getId() {
        return id;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Question getQuestion(Question question) {
        return this.questions.stream().filter(question::equals).findFirst().get();
    }

    public boolean isImageGame(){
        return this.type;
    }

    public int getRemainedPlayers() {
        return remainedPlayers;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public int getRounds() {
        return rounds;
    }

    public int getTimeQuestion() {
        return timeQuestion;
    }

    public static class GameBuilder {
        private int id;
        private boolean type;
        private int rounds;
        private int remainedPlayers;
        private int totalPlayers;
        private int timeQuestion;
        private boolean state;
        private List<Question> questions;

        public GameBuilder(int gameId) {
            this.id = gameId;
        }

        public GameBuilder type(boolean type) {
            this.type = type;
            return this;
        }

        public GameBuilder rounds(int rounds) {
            this.rounds = rounds;
            return this;
        }

        public GameBuilder remainedPlayers(int remainedPlayers) {
            this.remainedPlayers = remainedPlayers;
            return this;
        }

        public GameBuilder totalPlayers(int totalPlayers) {
            this.totalPlayers = totalPlayers;
            return this;
        }

        public GameBuilder timeQuestion(int timeQuestion) {
            this.timeQuestion = timeQuestion;
            return this;
        }

        public GameBuilder state(boolean state) {
            this.state = state;
            return this;
        }

        public GameBuilder questions(List<Question> questions){
            this.questions = questions;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }
}
