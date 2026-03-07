package it.alnao.javafx.photodispatcher;

import it.alnao.javafx.photodispatcher.controller.PhotoDispatcherController;
import javafx.application.Application;
import javafx.stage.Stage;

public class PhotoDispatcherApp extends Application {

    @Override
    public void start(Stage stage) {
        new PhotoDispatcherController().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}