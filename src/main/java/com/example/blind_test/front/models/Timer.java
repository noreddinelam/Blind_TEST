package com.example.blind_test.front.models;

import com.example.blind_test.front.controllers.GameController;

public class Timer extends Thread {
    private int cpt;
    private final GameController gameController;

    public Timer(int cpt, GameController gameController) {
        this.cpt = cpt;
        this.gameController = gameController;
    }

    @Override
    public void run() {
        try {
            while (cpt != 0) {
                this.wait(1000);
                cpt--;
                this.gameController.setTimerTime(cpt);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
