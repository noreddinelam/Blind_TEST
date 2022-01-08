package com.example.blind_test.shared.communication;

import com.example.blind_test.front.models.Player;

import java.util.List;

public class JoinGameType {
    private Player player;
    private List<Player> otherPlayers;

    public JoinGameType(Player player){
        this.player = player;
    }

    public JoinGameType(Player player,List<Player> otherPlayers){
        this.player = player;
        this.otherPlayers = otherPlayers;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Player> getOtherPlayers() {
        return otherPlayers;
    }
}
