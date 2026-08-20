package it.alnao.javafx.photocarousel;

import it.alnao.javafx.photocarousel.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main Application Class for 96-PhotoCarousel.
 */
public class PhotoCarouselApp extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AlNao Photo Carousel - JavaFX");

        mainController = new MainController(primaryStage);
        Pane root = mainController.buildUI();

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setScene(scene);

        // Fullscreen configuration as requested by user
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("Premi ESC per uscire da Schermo Intero");

        primaryStage.setOnCloseRequest(e -> {
            if (mainController != null) {
                mainController.cleanup();
            }
        });

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (mainController != null) {
            mainController.cleanup();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
