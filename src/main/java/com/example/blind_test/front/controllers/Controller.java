package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import javafx.scene.Scene;

public abstract class Controller {
    protected ClientImpl clientImpl;
    protected Scene scene;

    public void setNecessaryInformation(ClientImpl clientImpl,Scene scene){
        this.clientImpl = clientImpl;
        this.clientImpl.setMainMenuController(this);
        this.scene = scene;
    }
}
