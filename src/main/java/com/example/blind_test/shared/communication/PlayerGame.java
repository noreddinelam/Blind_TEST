package com.example.blind_test.shared.communication;

import com.example.blind_test.front.models.Game;
import com.example.blind_test.front.models.Player;

public class PlayerGame {
    private Game game;
    private Player player;

    public PlayerGame(Game game,Player player){
        this.game = game;
        this.player = player;
    }
}
