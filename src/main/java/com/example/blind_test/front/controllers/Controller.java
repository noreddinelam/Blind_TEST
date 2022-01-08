package com.example.blind_test.front.controllers;

import com.example.blind_test.HelloApplication;
import com.example.blind_test.client.ClientImpl;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public abstract class Controller {
    protected ClientImpl clientImpl;
    protected Scene scene;
    public void setNecessaryInformation(AsynchronousSocketChannel client, String ipAddress ,Scene scene){
        this.clientImpl.setController(this);
        this.clientImpl.setClient(client);
        this.clientImpl.setIpAddress(ipAddress);
        this.clientImpl.initThreadReader();
        this.clientImpl.initListOfFunctions();
        this.scene = scene;
    }

    public void backMainMenu()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainMenu.fxml"));
            Parent parent = fxmlLoader.load();
            MainMenuController controller = fxmlLoader.getController();
            controller.initializeListOfUnStartedGames();
            this.clientImpl.setController(controller);
            controller.scene=this.scene;
            this.scene.setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
