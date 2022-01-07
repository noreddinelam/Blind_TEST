package com.example.blind_test.front.controllers;

import com.example.blind_test.client.ClientImpl;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class Controller {
    protected ClientImpl clientImpl;
    protected Scene scene;

    public void setNecessaryInformation(AsynchronousSocketChannel client, String ipAddress ,Scene scene){
        this.clientImpl.setMainMenuController(this);
        this.clientImpl.setClient(client);
        this.clientImpl.setIpAddress(ipAddress);
        this.clientImpl.initThreadReader();
        this.clientImpl.initListOfFunctions();
        this.scene = scene;
    }

    public void commandFailed(String title, String failureMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(failureMessage);
            alert.showAndWait();
        });
    }
}
