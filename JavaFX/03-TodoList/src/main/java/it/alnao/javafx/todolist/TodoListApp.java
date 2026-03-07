package it.alnao.javafx.todolist;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Progetto 03 - To-Do List App
 * Concetto principale: ObservableList
 * 
 * Questo progetto introduce il concetto di ObservableList, una collezione
 * che notifica automaticamente i listener quando il suo contenuto cambia.
 * La ListView si aggiorna automaticamente senza bisogno di refresh manuale.
 */
public class TodoListApp extends Application {
    
    // ObservableList che mantiene la lista dei task
    private ObservableList<String> taskList;
    private ListView<String> listView;
    private TextField taskInput;

    @Override
    public void start(Stage primaryStage) {
        // Inizializza l'ObservableList con alcuni task predefiniti
        taskList = FXCollections.observableArrayList(
            "Studiare JavaFX",
            "Completare il progetto",
            "Fare la spesa"
        );

        // Crea il titolo
        Label titleLabel = new Label("📝 To-Do List");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        // Area input per nuovo task
        HBox inputBox = createInputArea();

        // ListView collegata all'ObservableList
        listView = new ListView<>(taskList);
        listView.setPrefHeight(300);
        listView.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );

        // Pulsanti di azione
        HBox actionBox = createActionButtons();

        // Contatore task
        Label counterLabel = new Label();
        updateCounter(counterLabel);
        counterLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");

        // Listener per aggiornare il contatore quando cambia la lista
        taskList.addListener((javafx.collections.ListChangeListener.Change<? extends String> c) -> {
            updateCounter(counterLabel);
        });

        // Layout principale
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        
        // Card contenitore
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setMaxWidth(500);
        card.getChildren().addAll(titleLabel, inputBox, listView, actionBox, counterLabel);

        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("To-Do List App - ObservableList Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crea l'area di input per inserire nuovi task
     */
    private HBox createInputArea() {
        taskInput = new TextField();
        taskInput.setPromptText("Inserisci un nuovo task...");
        taskInput.setPrefHeight(40);
        taskInput.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-padding: 5px 10px; " +
            "-fx-font-size: 14px;"
        );

        // Permette di aggiungere con Invio
        taskInput.setOnAction(e -> addTask());

        Button addButton = new Button("➕ Aggiungi");
        addButton.setPrefHeight(40);
        addButton.setStyle(
            "-fx-background-color: #0d6efd; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 5px 20px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        addButton.setOnAction(e -> addTask());
        
        // Effetto hover
        addButton.setOnMouseEntered(e -> 
            addButton.setStyle(addButton.getStyle() + "-fx-background-color: #0b5ed7;")
        );
        addButton.setOnMouseExited(e -> 
            addButton.setStyle(addButton.getStyle().replace("-fx-background-color: #0b5ed7;", "-fx-background-color: #0d6efd;"))
        );

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(taskInput, Priority.ALWAYS);
        hbox.getChildren().addAll(taskInput, addButton);
        
        return hbox;
    }

    /**
     * Crea i pulsanti di azione (Rimuovi, Completa, Cancella tutto)
     */
    private HBox createActionButtons() {
        Button removeButton = createStyledButton("🗑️ Rimuovi", "#dc3545", "#bb2d3b");
        removeButton.setOnAction(e -> removeSelectedTask());

        Button completeButton = createStyledButton("✓ Completa", "#198754", "#146c43");
        completeButton.setOnAction(e -> completeSelectedTask());

        Button clearButton = createStyledButton("🗑️ Cancella tutto", "#6c757d", "#5c636a");
        clearButton.setOnAction(e -> clearAllTasks());

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(removeButton, completeButton, clearButton);
        
        return hbox;
    }

    /**
     * Crea un pulsante con stile Bootstrap
     */
    private Button createStyledButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 8px 15px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        
        String baseStyle = button.getStyle();
        button.setOnMouseEntered(e -> 
            button.setStyle(baseStyle.replace(color, hoverColor))
        );
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
        return button;
    }

    /**
     * Aggiunge un nuovo task all'ObservableList
     * La ListView si aggiorna automaticamente!
     */
    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            taskList.add(task);  // Aggiunta automatica alla lista
            taskInput.clear();
        } else {
            showAlert("Attenzione", "Inserisci un task valido!");
        }
    }

    /**
     * Rimuove il task selezionato
     */
    private void removeSelectedTask() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            taskList.remove(selectedIndex);  // Rimozione automatica
        } else {
            showAlert("Attenzione", "Seleziona un task da rimuovere!");
        }
    }

    /**
     * Segna il task come completato (aggiunge un check)
     */
    private void completeSelectedTask() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String task = taskList.get(selectedIndex);
            if (!task.startsWith("✓ ")) {
                taskList.set(selectedIndex, "✓ " + task);  // Modifica automatica
            }
        } else {
            showAlert("Attenzione", "Seleziona un task da completare!");
        }
    }

    /**
     * Cancella tutti i task
     */
    private void clearAllTasks() {
        if (!taskList.isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma");
            confirm.setHeaderText("Cancellare tutti i task?");
            confirm.setContentText("Questa azione non può essere annullata.");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    taskList.clear();  // Pulizia automatica
                }
            });
        }
    }

    /**
     * Aggiorna il contatore dei task
     */
    private void updateCounter(Label label) {
        long completed = taskList.stream().filter(t -> t.startsWith("✓ ")).count();
        label.setText(String.format("Task totali: %d | Completati: %d | Da fare: %d",
            taskList.size(), completed, taskList.size() - completed));
    }

    /**
     * Mostra un alert di avviso
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
