package it.alnao.javafx.texteditor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;

/**
 * Progetto 05 - Simple Text Editor
 * Concetto principale: MenuBar e FileChooser
 * 
 * Editor di testo con menu File (Nuovo, Apri, Salva, Esci)
 * e operazioni di File I/O.
 */
public class TextEditorApp extends Application {
    
    private TextArea textArea;
    private File currentFile;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // TextArea principale
        textArea = new TextArea();
        textArea.setStyle(
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 14px; " +
            "-fx-background-color: white;"
        );

        // MenuBar
        MenuBar menuBar = createMenuBar();

        // Layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(textArea);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Simple Text Editor - Untitled");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crea la MenuBar con menu File ed Edit
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: white; -fx-border-color: #ced4da;");

        // Menu File
        Menu fileMenu = new Menu("📁 File");
        
        MenuItem newItem = new MenuItem("Nuovo");
        newItem.setOnAction(e -> newFile());
        
        MenuItem openItem = new MenuItem("Apri...");
        openItem.setOnAction(e -> openFile());
        
        MenuItem saveItem = new MenuItem("Salva");
        saveItem.setOnAction(e -> saveFile());
        
        MenuItem saveAsItem = new MenuItem("Salva con nome...");
        saveAsItem.setOnAction(e -> saveFileAs());
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        
        MenuItem exitItem = new MenuItem("Esci");
        exitItem.setOnAction(e -> exitApp());
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, separator, exitItem);

        // Menu Edit
        Menu editMenu = new Menu("✏️ Modifica");
        
        MenuItem cutItem = new MenuItem("Taglia");
        cutItem.setOnAction(e -> textArea.cut());
        
        MenuItem copyItem = new MenuItem("Copia");
        copyItem.setOnAction(e -> textArea.copy());
        
        MenuItem pasteItem = new MenuItem("Incolla");
        pasteItem.setOnAction(e -> textArea.paste());
        
        MenuItem selectAllItem = new MenuItem("Seleziona tutto");
        selectAllItem.setOnAction(e -> textArea.selectAll());
        
        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, new SeparatorMenuItem(), selectAllItem);

        // Menu Help
        Menu helpMenu = new Menu("❓ Aiuto");
        MenuItem aboutItem = new MenuItem("Info");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        return menuBar;
    }

    /**
     * Crea un nuovo file
     */
    private void newFile() {
        if (!textArea.getText().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Nuovo File");
            confirm.setHeaderText("Vuoi salvare le modifiche?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    saveFile();
                }
            });
        }
        textArea.clear();
        currentFile = null;
        primaryStage.setTitle("Simple Text Editor - Untitled");
    }

    /**
     * Apre un file con FileChooser
     */
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Apri File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("File di Testo", "*.txt"),
            new FileChooser.ExtensionFilter("Tutti i File", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                textArea.setText(content);
                currentFile = file;
                primaryStage.setTitle("Simple Text Editor - " + file.getName());
            } catch (IOException e) {
                showError("Errore durante l'apertura del file: " + e.getMessage());
            }
        }
    }

    /**
     * Salva il file corrente
     */
    private void saveFile() {
        if (currentFile == null) {
            saveFileAs();
        } else {
            try {
                Files.writeString(currentFile.toPath(), textArea.getText());
                showInfo("File salvato con successo!");
            } catch (IOException e) {
                showError("Errore durante il salvataggio: " + e.getMessage());
            }
        }
    }

    /**
     * Salva il file con un nuovo nome
     */
    private void saveFileAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva con Nome");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File di Testo", "*.txt")
        );
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                Files.writeString(file.toPath(), textArea.getText());
                currentFile = file;
                primaryStage.setTitle("Simple Text Editor - " + file.getName());
                showInfo("File salvato con successo!");
            } catch (IOException e) {
                showError("Errore durante il salvataggio: " + e.getMessage());
            }
        }
    }

    /**
     * Chiude l'applicazione
     */
    private void exitApp() {
        if (!textArea.getText().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Esci");
            confirm.setHeaderText("Vuoi salvare prima di uscire?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    saveFile();
                }
                primaryStage.close();
            });
        } else {
            primaryStage.close();
        }
    }

    /**
     * Mostra info sull'applicazione
     */
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Simple Text Editor v1.0");
        alert.setContentText("Progetto JavaFX 05 - MenuBar e FileChooser\n\n" +
                           "Editor di testo semplice con:\n" +
                           "- Apertura e salvataggio file\n" +
                           "- Operazioni di editing base\n" +
                           "- Menu completi");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
