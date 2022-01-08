package com.example.blind_test.shared.communication;

import com.example.blind_test.front.models.Player;
import com.example.blind_test.front.models.Question;

import java.util.List;

public class NextRoundInformation {
    private Question question;
    private List<Player> players;
    private int questionOrder;

    public NextRoundInformation(List<Player> players,Question question,int questionOrder){
        this.players = players;
        this.question = question;
        this.questionOrder = questionOrder;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }
}
