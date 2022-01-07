package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import javafx.scene.Scene;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class Controller {
    protected ClientImpl clientImpl;
    protected Scene scene;

    public void setNecessaryInformation(AsynchronousSocketChannel client, String ipAddress ,Scene scene){
        this.clientImpl = ClientImpl.getUniqueInstanceClientImpl();
        this.clientImpl.setMainMenuController(this);
        this.clientImpl.setClient(client);
        this.clientImpl.setIpAddress(ipAddress);
        this.scene = scene;
    }
}
