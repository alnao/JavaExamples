package it.alnao.javafx.photocarousel.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal window for managing configured image folders (400x400 initial size, sorted alphabetically).
 */
public class FolderConfigDialog {

    private final Stage dialogStage;
    private final ObservableList<String> folderObservableList;
    private boolean saved = false;

    public FolderConfigDialog(Stage parentStage, List<String> currentFolders) {
        dialogStage = new Stage();
        dialogStage.initOwner(parentStage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Configurazione Cartelle Immagini");

        List<String> initialList = new ArrayList<>(currentFolders != null ? currentFolders : new ArrayList<>());
        initialList.sort(String.CASE_INSENSITIVE_ORDER);

        folderObservableList = FXCollections.observableArrayList(initialList);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #1e1e2e; -fx-font-family: 'Segoe UI', sans-serif;");

        Label titleLabel = new Label("📁 Lista Cartelle Configurate");
        titleLabel.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 15px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>(folderObservableList);
        listView.setPrefHeight(220);
        listView.setStyle("-fx-background-color: #181825; -fx-control-inner-background: #181825; -fx-text-fill: #cdd6f4; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #313244;");

        Button btnAdd = new Button("➕ Aggiungi");
        btnAdd.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 12px;");

        Button btnRemove = new Button("🗑️ Rimuovi");
        btnRemove.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 12px;");

        HBox actionsBox = new HBox(10, btnAdd, btnRemove);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        Button btnSave = new Button("💾 Salva");
        btnSave.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 16px;");

        Button btnCancel = new Button("Annulla");
        btnCancel.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 16px;");

        HBox footerBox = new HBox(10, btnSave, btnCancel);
        footerBox.setAlignment(Pos.CENTER_RIGHT);

        btnAdd.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Seleziona Cartella Immagini");
            File selected = chooser.showDialog(dialogStage);
            if (selected != null) {
                String path = selected.getAbsolutePath();
                if (!folderObservableList.contains(path)) {
                    folderObservableList.add(path);
                    folderObservableList.sort(String.CASE_INSENSITIVE_ORDER);
                }
            }
        });

        btnRemove.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                folderObservableList.remove(selected);
            }
        });

        btnSave.setOnAction(e -> {
            saved = true;
            dialogStage.close();
        });

        btnCancel.setOnAction(e -> {
            saved = false;
            dialogStage.close();
        });

        root.getChildren().addAll(titleLabel, listView, actionsBox, footerBox);

        Scene scene = new Scene(root, 400, 400);
        dialogStage.setScene(scene);
        dialogStage.setWidth(400);
        dialogStage.setHeight(400);
    }

    public boolean showAndWait() {
        dialogStage.showAndWait();
        return saved;
    }

    public List<String> getFolders() {
        List<String> list = new ArrayList<>(folderObservableList);
        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }
}
