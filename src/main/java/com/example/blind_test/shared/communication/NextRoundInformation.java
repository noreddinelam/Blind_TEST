package com.example.blind_test.shared.communication;

import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;

import java.util.List;

public class NextRoundInformation {
    private Question question;
    private List<Player> players;

    public NextRoundInformation(List<Player> players,Question question){
        this.players = players;
        this.question = question;
    }
}
