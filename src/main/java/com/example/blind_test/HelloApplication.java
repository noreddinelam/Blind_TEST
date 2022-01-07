package com.example.blind_test;

import com.example.blind_test.client.ClientImpl;
import com.example.blind_test.front.controllers.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainMenu.fxml"));
        Parent parent = fxmlLoader.load();
        MainMenuController controller = fxmlLoader.getController();
        Scene scene = new Scene(parent);
        controller.setNecessaryInformation(ClientImpl.getUniqueInstanceClientImpl(),scene);
        stage.setTitle("Blind Test!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
