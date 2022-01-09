package com.example.blind_test.front.models;

import com.example.blind_test.front.controllers.GameController;

public class Timer extends Thread {
    private final GameController gameController;
    private int cpt;
    private boolean interrupt = false;

    public Timer(int cpt, GameController gameController) {
        this.cpt = cpt;
        this.gameController = gameController;
    }

    @Override
    public void run() {
        try {
            while (cpt != 0 && !interrupt) {
                Thread.sleep(1000);
                cpt--;
                this.gameController.setTimerTime(cpt);
            }
            if (!interrupt)
                this.gameController.nextRound();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }
}
