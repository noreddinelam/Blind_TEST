package com.example.blind_test.front.models;

import java.util.List;

public class Game {
    private int id;
    private boolean type;
    private int rounds;
    private int players;
    private int timeQuestion;
    private boolean state;
    private List<Question> questions;

    private Game(GameBuilder gb) {
        this.id = gb.id;
        this.type = gb.type;
        this.rounds = gb.rounds;
        this.players = gb.players;
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

    public int getPlayers() {
        return players;
    }

    public static class GameBuilder {
        private int id;
        private boolean type;
        private int rounds;
        private int players;
        private int timeQuestion;
        private boolean state;

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

        public GameBuilder players(int players) {
            this.players = players;
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

        public Game build() {
            return new Game(this);
        }
    }
}
