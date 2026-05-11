package it.alnao.javafx.controlroom;

import it.alnao.javafx.controlroom.controller.ControlRoomController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * AlNao Sh Control Room - Main Application Entry Point
 * 
 * A JavaFX "control room" for monitoring server environments
 * and executing shell scripts with real-time output.
 */
public class ControlRoomApp extends Application {

    @Override
    public void start(Stage stage) {
        new ControlRoomController().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
