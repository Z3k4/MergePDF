package com.mezkay.mergepdf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MergePDFApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MergePDFApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MergePDF");
        stage.setScene(scene);
        stage.setResizable(false);

        AppController appController = fxmlLoader.getController();
        appController.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}