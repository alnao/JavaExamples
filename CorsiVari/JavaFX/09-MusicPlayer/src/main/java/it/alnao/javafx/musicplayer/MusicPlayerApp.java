package it.alnao.javafx.musicplayer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;

/**
 * Progetto 09 - MP3 Music Player
 * Concetto principale: MediaPlayer e MediaView
 * 
 * Lettore musicale con controlli play/pause/stop, volume e timeline.
 */
public class MusicPlayerApp extends Application {
    
    private MediaPlayer mediaPlayer;
    private Slider volumeSlider;
    private Slider timeSlider;
    private Button playPauseButton;
    private Label titleLabel;
    private Label timeLabel;
    private boolean isPlaying = false;

    @Override
    public void start(Stage primaryStage) {
        // Titolo
        Label appTitleLabel = new Label("🎵 MP3 Music Player");
        appTitleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        // Label titolo canzone
        titleLabel = new Label("Nessun file caricato");
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-text-fill: #0d6efd; " +
            "-fx-font-weight: bold;"
        );

        // Time slider
        timeSlider = new Slider();
        timeSlider.setPrefWidth(500);
        timeSlider.setDisable(true);
        
        // Time label
        timeLabel = new Label("00:00 / 00:00");
        timeLabel.setStyle("-fx-text-fill: #6c757d;");

        // Pulsanti controllo
        HBox controlBox = createControlButtons();

        // Volume slider
        Label volumeLabel = new Label("🔊 Volume:");
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(200);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(25);
        
        HBox volumeBox = new HBox(10);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.getChildren().addAll(volumeLabel, volumeSlider);

        // Pulsante carica file
        Button loadButton = new Button("📁 Carica MP3");
        loadButton.setStyle(
            "-fx-background-color: #198754; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        loadButton.setOnAction(e -> loadMediaFile(primaryStage));

        // Card layout
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setMaxWidth(600);
        card.getChildren().addAll(
            appTitleLabel, titleLabel, timeSlider, timeLabel, 
            controlBox, volumeBox, loadButton
        );

        // Layout principale
        VBox root = new VBox();
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.getChildren().add(card);

        Scene scene = new Scene(root, 700, 550);
        primaryStage.setTitle("MP3 Music Player - MediaPlayer Demo");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
        primaryStage.show();
    }

    /**
     * Crea i pulsanti di controllo
     */
    private HBox createControlButtons() {
        playPauseButton = new Button("▶️ Play");
        playPauseButton.setDisable(true);
        playPauseButton.setStyle(
            "-fx-background-color: #0d6efd; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-padding: 12px 25px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        playPauseButton.setOnAction(e -> togglePlayPause());

        Button stopButton = new Button("⏹️ Stop");
        stopButton.setStyle(
            "-fx-background-color: #dc3545; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-padding: 12px 25px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        stopButton.setOnAction(e -> stopPlayback());

        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(playPauseButton, stopButton);
        
        return hbox;
    }

    /**
     * Carica un file MP3
     */
    private void loadMediaFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file MP3");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File Audio", "*.mp3", "*.wav", "*.m4a")
        );
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            
            try {
                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                
                // Setup MediaPlayer
                setupMediaPlayer();
                
                titleLabel.setText(file.getName());
                playPauseButton.setDisable(false);
                timeSlider.setDisable(false);
                
            } catch (Exception e) {
                showError("Errore nel caricamento del file: " + e.getMessage());
            }
        }
    }

    /**
     * Setup MediaPlayer con listener
     */
    private void setupMediaPlayer() {
        // Volume binding
        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
        
        // Listener per aggiornare il time slider
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(newTime.toSeconds());
            }
            updateTimeLabel();
        });

        // Setup del time slider quando il media è pronto
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getTotalDuration();
            timeSlider.setMax(totalDuration.toSeconds());
            updateTimeLabel();
        });

        // Time slider seek
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (timeSlider.isValueChanging() && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
            }
        });

        // Auto-stop alla fine
        mediaPlayer.setOnEndOfMedia(() -> {
            stopPlayback();
        });
    }

    /**
     * Toggle Play/Pause
     */
    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseButton.setText("▶️ Play");
                isPlaying = false;
            } else {
                mediaPlayer.play();
                playPauseButton.setText("⏸️ Pause");
                isPlaying = true;
            }
        }
    }

    /**
     * Stop playback
     */
    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playPauseButton.setText("▶️ Play");
            isPlaying = false;
            timeSlider.setValue(0);
        }
    }

    /**
     * Aggiorna label del tempo
     */
    private void updateTimeLabel() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalDuration = mediaPlayer.getTotalDuration();
            
            timeLabel.setText(
                formatDuration(currentTime) + " / " + formatDuration(totalDuration)
            );
        }
    }

    /**
     * Formatta Duration in mm:ss
     */
    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
