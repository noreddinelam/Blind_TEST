package com.example.blind_test.shared.communication;

import java.util.Objects;

public class Credentials {
    private String username;
    private int gameId;

    public Credentials(String username,int gameId){
        this.username = username;
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return gameId == that.gameId && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, gameId);
    }
}
