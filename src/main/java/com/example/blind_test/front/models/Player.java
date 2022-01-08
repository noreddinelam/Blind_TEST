package com.example.blind_test.front.models;

import java.util.Objects;

public class Player {
    private String username;
    private Game game;
    private int score;

    public Player(String username, Game game) {
        this.username = username;
        this.game = game;
        this.score = 0;
    }

    public Player(String username, Game game, int score) {
        this.username = username;
        this.game = game;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
