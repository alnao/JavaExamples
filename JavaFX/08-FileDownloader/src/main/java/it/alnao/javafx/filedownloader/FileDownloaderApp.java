package it.alnao.javafx.filedownloader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Progetto 08 - Async File Downloader
 * Concetto principale: Task e Concurrency
 * 
 * Downloader simulato per dimostrare Task asincroni,
 * ProgressBar e Platform.runLater().
 */
public class FileDownloaderApp extends Application {
    
    private ProgressBar progressBar;
    private Label statusLabel;
    private Button downloadButton;
    private TextField urlField;
    private TextField pathField;
    private File downloadDirectory;

    @Override
    public void start(Stage primaryStage) {
        // Inizializza directory di download (HOME di default)
        String homeDir = System.getProperty("user.home");
        downloadDirectory = new File(homeDir);
        
        // Titolo
        Label titleLabel = new Label("⬇️ Async File Downloader");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        // URL input
        Label urlLabel = new Label("URL:");
        urlField = new TextField("https://speed.hetzner.de/100MB.bin");
        urlField.setPrefWidth(400);
        urlField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-padding: 8px 12px;"
        );

        HBox urlBox = new HBox(10);
        urlBox.setAlignment(Pos.CENTER);
        urlBox.getChildren().addAll(urlLabel, urlField);

        // Path selector
        Label pathLabel = new Label("Cartella:");
        pathField = new TextField(downloadDirectory.getAbsolutePath());
        pathField.setEditable(false);
        pathField.setPrefWidth(350);
        pathField.setStyle(
            "-fx-background-color: #e9ecef; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-padding: 8px 12px;"
        );
        
        Button browseButton = new Button("📁 Sfoglia");
        browseButton.setStyle(
            "-fx-background-color: #6c757d; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 8px 15px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        browseButton.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Seleziona Cartella di Download");
            dirChooser.setInitialDirectory(downloadDirectory);
            File selectedDir = dirChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                downloadDirectory = selectedDir;
                pathField.setText(selectedDir.getAbsolutePath());
            }
        });

        HBox pathBox = new HBox(10);
        pathBox.setAlignment(Pos.CENTER);
        pathBox.getChildren().addAll(pathLabel, pathField, browseButton);

        // ProgressBar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(500);
        progressBar.setPrefHeight(30);
        progressBar.setStyle(
            "-fx-accent: #0d6efd;"
        );

        // Status label
        statusLabel = new Label("Pronto per il download");
        statusLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");

        // Pulsante Download
        downloadButton = new Button("⬇️ Avvia Download");
        downloadButton.setStyle(
            "-fx-background-color: #0d6efd; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 30px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        downloadButton.setOnAction(e -> startDownload());

        // Pulsante Cancella
        Button cancelButton = new Button("❌ Cancella");
        cancelButton.setStyle(
            "-fx-background-color: #dc3545; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        cancelButton.setOnAction(e -> {
            progressBar.setProgress(0);
            statusLabel.setText("Download cancellato");
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(downloadButton, cancelButton);

        // Card layout
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setMaxWidth(700);
        card.getChildren().addAll(titleLabel, urlBox, pathBox, progressBar, statusLabel, buttonBox);

        // Layout principale
        VBox root = new VBox();
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.getChildren().add(card);

        Scene scene = new Scene(root, 850, 550);
        primaryStage.setTitle("Async File Downloader - Task Concurrency Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Avvia il download in un Task asincrono
     */
    private void startDownload() {
        String urlString = urlField.getText().trim();
        if (urlString.isEmpty()) {
            statusLabel.setText("⚠ Inserisci un URL valido!");
            statusLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 14px;");
            return;
        }
        
        downloadButton.setDisable(true);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        statusLabel.setText("Connessione in corso...");
        statusLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");

        // Crea un Task asincrono per il download reale
        Task<File> downloadTask = new Task<File>() {
            @Override
            protected File call() throws Exception {
                // Estrai nome file dall'URL
                String tempFileName = urlString.substring(urlString.lastIndexOf('/') + 1);
                final String fileName = (tempFileName.isEmpty() || !tempFileName.contains(".")) 
                    ? "downloaded_file.bin" 
                    : tempFileName;
                File outputFile = new File(downloadDirectory, fileName);
                
                // Apri connessione
                URL url = new URL(urlString);
                var connection = url.openConnection();
                long fileSize = connection.getContentLengthLong();
                
                Platform.runLater(() -> 
                    statusLabel.setText(String.format("Download di %s (%.2f MB)...", 
                        fileName, fileSize / 1024.0 / 1024.0))
                );
                
                // Download con InputStream e OutputStream
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(outputFile)) {
                    
                    byte[] buffer = new byte[8192];
                    long downloaded = 0;
                    int bytesRead;
                    
                    while ((bytesRead = in.read(buffer)) != -1) {
                        if (isCancelled()) {
                            out.close();
                            outputFile.delete();
                            break;
                        }
                        
                        out.write(buffer, 0, bytesRead);
                        downloaded += bytesRead;
                        
                        // Aggiorna progresso
                        if (fileSize > 0) {
                            long finalDownloaded = downloaded;
                            updateProgress(downloaded, fileSize);
                            Platform.runLater(() -> {
                                double percent = (finalDownloaded * 100.0) / fileSize;
                                statusLabel.setText(String.format(
                                    "Download: %.2f MB / %.2f MB (%.1f%%)",
                                    finalDownloaded / 1024.0 / 1024.0,
                                    fileSize / 1024.0 / 1024.0,
                                    percent
                                ));
                            });
                        }
                    }
                }
                
                return outputFile;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                File downloadedFile = getValue();
                Platform.runLater(() -> {
                    statusLabel.setText(String.format("✓ Download completato: %s", 
                        downloadedFile.getName()));
                    statusLabel.setStyle("-fx-text-fill: #198754; -fx-font-size: 14px; -fx-font-weight: bold;");
                    downloadButton.setDisable(false);
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    Throwable ex = getException();
                    statusLabel.setText("✗ Errore: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 14px; -fx-font-weight: bold;");
                    downloadButton.setDisable(false);
                });
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                Platform.runLater(() -> {
                    statusLabel.setText("Download cancellato");
                    statusLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
                    downloadButton.setDisable(false);
                });
            }
        };

        // Bind progressBar al Task
        progressBar.progressProperty().bind(downloadTask.progressProperty());

        // Avvia il Task in un thread separato
        Thread downloadThread = new Thread(downloadTask);
        downloadThread.setDaemon(true);
        downloadThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
