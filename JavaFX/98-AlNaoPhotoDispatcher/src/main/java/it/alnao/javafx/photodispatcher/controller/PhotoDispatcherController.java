package it.alnao.javafx.photodispatcher.controller;

import it.alnao.javafx.photodispatcher.model.FolderConfig;
import it.alnao.javafx.photodispatcher.service.ConfigService;
import it.alnao.javafx.photodispatcher.service.FileMoveService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PhotoDispatcherController {

    private final ConfigService configService = new ConfigService();
    private final FileMoveService fileMoveService = new FileMoveService();

    private final List<File> imageFiles = new ArrayList<>();
    private int currentImageIndex = 0;

    private Stage primaryStage;
    private ImageView imageView;
    private Label statusLabel;
    private Label currentFileLabel;
    private Label activeConfigLabel;
    private TreeView<File> treeViewLeft;
    private TreeView<File> treeViewRight;
    private Button prevButton;
    private Button nextButton;
    private Menu configMenu;

    public void start(Stage stage) {
        this.primaryStage = stage;

        configService.loadConfig();

        BorderPane root = new BorderPane();

        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = createToolBar();
        VBox topContainer = new VBox(menuBar, toolBar);
        root.setTop(topContainer);

        SplitPane splitPane = new SplitPane();
        VBox leftPane = createLeftPane();
        BorderPane rightPane = createRightPane();

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.7);

        root.setCenter(splitPane);

        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #e9ecef;");
        statusLabel = new Label("Pronto");
        statusBar.getChildren().add(statusLabel);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("AlNao Photo Dispatcher");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        refreshImages();
        refreshDestinationTree();
    }

    private ToolBar createToolBar() {
        activeConfigLabel = new Label();
        updateActiveConfigLabel();
        return new ToolBar(new Label("Configurazione attiva:"), activeConfigLabel);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem configItem = new MenuItem("Configurazione Cartelle...");
        configItem.setOnAction(e -> showConfigDialog());

        MenuItem refreshItem = new MenuItem("Aggiorna Tutto");
        refreshItem.setOnAction(e -> {
            refreshImages();
            refreshDestinationTree();
        });

        MenuItem exitItem = new MenuItem("Esci");
        exitItem.setOnAction(e -> primaryStage.close());

        fileMenu.getItems().addAll(configItem, refreshItem, new SeparatorMenuItem(), exitItem);

        configMenu = new Menu("Configurazioni");
        rebuildConfigMenu();

        menuBar.getMenus().addAll(fileMenu, configMenu);
        return menuBar;
    }

    private VBox createLeftPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #2b2b2b;");

        currentFileLabel = new Label("Nessuna immagine");
        currentFileLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(600);
        imageView.setFitWidth(800);

        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);

        prevButton = new Button("<< Precedente");
        prevButton.setOnAction(e -> showPreviousImage());

        Button deleteBtn = new Button("Elimina");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> deleteCurrentImage());

        nextButton = new Button("Successiva >>");
        nextButton.setOnAction(e -> showNextImage());

        navBox.getChildren().addAll(prevButton, deleteBtn, nextButton);
        box.getChildren().addAll(currentFileLabel, imageView, navBox);

        imageView.fitWidthProperty().bind(box.widthProperty().subtract(20));
        imageView.fitHeightProperty().bind(box.heightProperty().subtract(100));
        return box;
    }

    private BorderPane createRightPane() {
        BorderPane rightPane = new BorderPane();

        Label title = new Label("Cartelle Destinazione:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10;");
        rightPane.setTop(title);

        treeViewLeft = createConfiguredTreeView();
        treeViewRight = createConfiguredTreeView();

        treeViewLeft.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) treeViewRight.getSelectionModel().clearSelection();
        });
        treeViewRight.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) treeViewLeft.getSelectionModel().clearSelection();
        });

        SplitPane splitPane = new SplitPane(treeViewLeft, treeViewRight);
        splitPane.setDividerPositions(0.5);
        rightPane.setCenter(splitPane);

        Button moveBtn = new Button("Sposta nella cartella selezionata");
        moveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 10;");
        moveBtn.setOnAction(e -> {
            TreeItem<File> selected = getSelectedFolder();
            if (selected != null) {
                moveCurrentImageTo(selected.getValue());
            } else {
                showError("Seleziona una cartella di destinazione");
            }
        });

        Button moveToRootBtn = new Button("Sposta nella cartella root");
        moveToRootBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 10;");
        moveToRootBtn.setOnAction(e -> moveCurrentImageToDestinationRoot());

        HBox moveButtonsBox = new HBox(8, moveBtn, moveToRootBtn);
        moveButtonsBox.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(10, moveButtonsBox);
        bottomBox.setPadding(new Insets(10));
        rightPane.setBottom(bottomBox);

        return rightPane;
    }

    private TreeView<File> createConfiguredTreeView() {
        TreeView<File> tv = new TreeView<>();
        tv.setShowRoot(false);

        tv.setCellFactory(p -> new TreeCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        tv.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<File> selected = tv.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    moveCurrentImageTo(selected.getValue());
                }
            }
        });
        return tv;
    }

    private TreeItem<File> getSelectedFolder() {
        TreeItem<File> left = treeViewLeft.getSelectionModel().getSelectedItem();
        if (left != null) return left;
        return treeViewRight.getSelectionModel().getSelectedItem();
    }

    private void rebuildConfigMenu() {
        if (configMenu == null) {
            return;
        }

        configMenu.getItems().clear();
        ToggleGroup group = new ToggleGroup();
        FolderConfig activeConfig = configService.getActiveConfig();

        for (FolderConfig cfg : configService.getFolderConfigs()) {
            RadioMenuItem item = new RadioMenuItem(cfg.getName());
            item.setToggleGroup(group);
            item.setSelected(activeConfig != null && activeConfig.getName().equals(cfg.getName()));
            item.setOnAction(e -> {
                configService.setActiveConfig(cfg);
                saveConfigWithErrorHandling();
                updateActiveConfigLabel();
                refreshImages();
                refreshDestinationTree();
            });
            configMenu.getItems().add(item);
        }
    }

    private void updateActiveConfigLabel() {
        FolderConfig activeConfig = configService.getActiveConfig();
        if (activeConfigLabel != null) {
            activeConfigLabel.setText(activeConfig != null ? activeConfig.getName() : "-");
        }
    }

    private void showConfigDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Configurazione Cartelle");
        dialog.setHeaderText("Gestisci la lista configurazioni (nome, sorgente, destinazione)");

        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        ListView<FolderConfig> configListView = new ListView<>();
        configListView.getItems().addAll(configService.getFolderConfigs());
        FolderConfig activeConfig = configService.getActiveConfig();
        if (activeConfig != null) {
            configListView.getItems().stream()
                    .filter(cfg -> cfg.getName().equals(activeConfig.getName()))
                    .findFirst()
                    .ifPresent(cfg -> configListView.getSelectionModel().select(cfg));
        }
        configListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(FolderConfig item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " | " + item.getSourcePath() + " -> " + item.getDestPath());
                }
            }
        });

        HBox buttons = new HBox(10);
        Button addBtn = new Button("Aggiungi");
        Button editBtn = new Button("Modifica");
        Button removeBtn = new Button("Rimuovi");
        buttons.getChildren().addAll(addBtn, editBtn, removeBtn);

        addBtn.setOnAction(e -> {
            FolderConfig newCfg = showEditConfigDialog(dialog.getOwner(), null, configListView.getItems());
            if (newCfg != null) {
                configListView.getItems().add(newCfg);
                configListView.getSelectionModel().select(newCfg);
            }
        });

        editBtn.setOnAction(e -> {
            FolderConfig selected = configListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Seleziona una configurazione da modificare");
                return;
            }
            FolderConfig edited = showEditConfigDialog(dialog.getOwner(), selected, configListView.getItems());
            if (edited != null) {
                int idx = configListView.getItems().indexOf(selected);
                configListView.getItems().set(idx, edited);
                configListView.getSelectionModel().select(edited);
            }
        });

        removeBtn.setOnAction(e -> {
            FolderConfig selected = configListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                configListView.getItems().remove(selected);
            }
        });

        configListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FolderConfig selected = configListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    FolderConfig edited = showEditConfigDialog(dialog.getOwner(), selected, configListView.getItems());
                    if (edited != null) {
                        int idx = configListView.getItems().indexOf(selected);
                        configListView.getItems().set(idx, edited);
                        configListView.getSelectionModel().select(edited);
                    }
                }
            }
        });

        content.getChildren().addAll(new Label("Doppio click per modificare una configurazione"), configListView, buttons);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (configListView.getItems().isEmpty()) {
                    showError("Aggiungi almeno una configurazione");
                    return null;
                }

                configService.setFolderConfigs(new ArrayList<>(configListView.getItems()));

                FolderConfig selectedInDialog = configListView.getSelectionModel().getSelectedItem();
                if (selectedInDialog != null) {
                    configService.setActiveConfig(configService.findByName(selectedInDialog.getName()).orElse(configService.getFolderConfigs().get(0)));
                } else {
                    FolderConfig currentActive = configService.getActiveConfig();
                    boolean activeExists = currentActive != null &&
                            configService.getFolderConfigs().stream().anyMatch(cfg -> cfg.getName().equals(currentActive.getName()));
                    if (!activeExists) {
                        configService.setActiveConfig(configService.getFolderConfigs().get(0));
                    }
                }

                saveConfigWithErrorHandling();
                rebuildConfigMenu();
                updateActiveConfigLabel();
                refreshImages();
                refreshDestinationTree();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private FolderConfig showEditConfigDialog(Window owner, FolderConfig configToEdit, List<FolderConfig> currentConfigs) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(configToEdit == null ? "Nuova Configurazione" : "Modifica Configurazione");

        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField nameField = new TextField(configToEdit != null ? configToEdit.getName() : "");
        TextField sourceField = new TextField(configToEdit != null ? configToEdit.getSourcePath() : "");
        TextField destField = new TextField(configToEdit != null ? configToEdit.getDestPath() : "");

        Button sourceBtn = new Button("Sfoglia...");
        sourceBtn.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            File sourceDir = new File(sourceField.getText());
            dc.setInitialDirectory(sourceDir.exists() ? sourceDir : null);
            File f = dc.showDialog(dialog.getOwner());
            if (f != null) sourceField.setText(f.getAbsolutePath() + "/");
        });

        Button destBtn = new Button("Sfoglia...");
        destBtn.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            File destDir = new File(destField.getText());
            dc.setInitialDirectory(destDir.exists() ? destDir : null);
            File f = dc.showDialog(dialog.getOwner());
            if (f != null) destField.setText(f.getAbsolutePath() + "/");
        });

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Sorgente:"), 0, 1);
        grid.add(sourceField, 1, 1);
        grid.add(sourceBtn, 2, 1);
        grid.add(new Label("Destinazione:"), 0, 2);
        grid.add(destField, 1, 2);
        grid.add(destBtn, 2, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            String name = nameField.getText() != null ? nameField.getText().trim() : "";
            String source = sourceField.getText() != null ? sourceField.getText().trim() : "";
            String dest = destField.getText() != null ? destField.getText().trim() : "";

            if (name.isEmpty() || source.isEmpty() || dest.isEmpty()) {
                showError("Nome, sorgente e destinazione sono obbligatori");
                return null;
            }

            boolean duplicate = currentConfigs.stream().anyMatch(cfg ->
                    cfg.getName().equals(name) && (configToEdit == null || !cfg.getName().equals(configToEdit.getName())));
            if (duplicate) {
                showError("Esiste già una configurazione con questo nome");
                return null;
            }

            return new FolderConfig(name, source, dest);
        }
        return null;
    }

    private void refreshImages() {
        imageFiles.clear();
        FolderConfig activeConfig = configService.getActiveConfig();
        String sourcePath = activeConfig != null ? activeConfig.getSourcePath() : "";
        File sourceDir = new File(sourcePath);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            statusLabel.setText("Cartella sorgente non valida: " + sourcePath);
            return;
        }

        File[] files = sourceDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                    lower.endsWith(".png") || lower.endsWith(".gif") ||
                    lower.endsWith(".bmp");
        });

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            imageFiles.addAll(Arrays.asList(files));
        }

        currentImageIndex = 0;
        showCurrentImage();
        statusLabel.setText("Trovate " + imageFiles.size() + " immagini in " + sourcePath);
    }

    private void refreshDestinationTree() {
        FolderConfig activeConfig = configService.getActiveConfig();
        String destPath = activeConfig != null ? activeConfig.getDestPath() : "";
        File destDir = new File(destPath);
        if (!destDir.exists() || !destDir.isDirectory()) {
            treeViewLeft.setRoot(null);
            treeViewRight.setRoot(null);
            return;
        }

        TreeItem<File> rootLeft = new TreeItem<>(destDir);
        rootLeft.setExpanded(true);

        TreeItem<File> rootRight = new TreeItem<>(destDir);
        rootRight.setExpanded(true);

        File[] subDirs = destDir.listFiles(File::isDirectory);
        if (subDirs != null) {
            Arrays.sort(subDirs, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            int mid = (int) Math.ceil(subDirs.length / 2.0);

            for (int i = 0; i < subDirs.length; i++) {
                TreeItem<File> item = new TreeItem<>(subDirs[i]);
                item.setExpanded(true);
                populateTree(item);

                if (i < mid) {
                    rootLeft.getChildren().add(item);
                } else {
                    rootRight.getChildren().add(item);
                }
            }
        }

        treeViewLeft.setRoot(rootLeft);
        treeViewRight.setRoot(rootRight);
    }

    private void populateTree(TreeItem<File> parentItem) {
        File parentFile = parentItem.getValue();
        File[] subDirs = parentFile.listFiles(File::isDirectory);

        if (subDirs != null) {
            Arrays.sort(subDirs, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            for (File subDir : subDirs) {
                TreeItem<File> childItem = new TreeItem<>(subDir);
                childItem.setExpanded(true);
                populateTree(childItem);
                parentItem.getChildren().add(childItem);
            }
        }
    }

    private void showCurrentImage() {
        if (imageFiles.isEmpty()) {
            imageView.setImage(null);
            currentFileLabel.setText("Nessuna immagine nella cartella sorgente");
            prevButton.setDisable(true);
            nextButton.setDisable(true);
            return;
        }

        if (currentImageIndex < 0) currentImageIndex = 0;
        if (currentImageIndex >= imageFiles.size()) currentImageIndex = imageFiles.size() - 1;

        File file = imageFiles.get(currentImageIndex);
        try {
            Image image = new Image(new FileInputStream(file));
            imageView.setImage(image);
            currentFileLabel.setText(file.getName() + " (" + (currentImageIndex + 1) + "/" + imageFiles.size() + ")");

            prevButton.setDisable(currentImageIndex == 0);
            nextButton.setDisable(currentImageIndex == imageFiles.size() - 1);
        } catch (FileNotFoundException e) {
            showError("Impossibile caricare immagine: " + file.getName());
        }
    }

    private void showNextImage() {
        if (currentImageIndex < imageFiles.size() - 1) {
            currentImageIndex++;
            showCurrentImage();
        }
    }

    private void showPreviousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--;
            showCurrentImage();
        }
    }

    private void moveCurrentImageToDestinationRoot() {
        FolderConfig activeConfig = configService.getActiveConfig();
        if (activeConfig == null) {
            showError("Nessuna configurazione attiva");
            return;
        }

        File destinationRoot = new File(activeConfig.getDestPath());
        if (!destinationRoot.exists() || !destinationRoot.isDirectory()) {
            showError("Cartella destinazione non valida: " + activeConfig.getDestPath());
            return;
        }

        moveCurrentImageTo(destinationRoot);
    }

    private void moveCurrentImageTo(File targetFolder) {
        if (imageFiles.isEmpty()) return;

        File currentFile = imageFiles.get(currentImageIndex);

        try {
            Path moved = fileMoveService.moveTo(currentFile.toPath(), targetFolder.toPath());
            statusLabel.setText("Spostato: " + currentFile.getName() + " -> " + targetFolder.getName() +
                    (moved.getFileName().equals(currentFile.toPath().getFileName()) ? "" : " (rinominato: " + moved.getFileName() + ")"));
            imageFiles.remove(currentImageIndex);

            if (currentImageIndex >= imageFiles.size()) {
                currentImageIndex = Math.max(0, imageFiles.size() - 1);
            }

            showCurrentImage();
        } catch (IOException e) {
            showError("Errore durante lo spostamento: " + e.getMessage());
        }
    }

    private void deleteCurrentImage() {
        if (imageFiles.isEmpty()) return;

        File currentFile = imageFiles.get(currentImageIndex);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'immagine?");
        alert.setContentText("Sei sicuro di voler eliminare: " + currentFile.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Files.delete(currentFile.toPath());
                statusLabel.setText("Eliminato: " + currentFile.getName());
                imageFiles.remove(currentImageIndex);

                if (currentImageIndex >= imageFiles.size()) {
                    currentImageIndex = Math.max(0, imageFiles.size() - 1);
                }

                showCurrentImage();
            } catch (IOException e) {
                showError("Errore durante l'eliminazione: " + e.getMessage());
            }
        }
    }

    private void saveConfigWithErrorHandling() {
        try {
            configService.saveConfig();
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setContentText(message);
        alert.showAndWait();
    }
}