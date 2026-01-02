package it.alnao.javafx.photodispatcher;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhotoDispatcherApp extends Application {

    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.alnaoPhotoDispatcher";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.properties";
    
    // Default paths richiesti
    private static final String DEFAULT_SOURCE_PATH = "/home/alnao/images/source/";
    private static final String DEFAULT_DEST_PATH = "/home/alnao/images/destination/";

    private String sourcePath;
    private String destPath;
    
    private List<File> imageFiles = new ArrayList<>();
    private int currentImageIndex = 0;
    
    private Stage primaryStage;
    private ImageView imageView;
    private Label statusLabel;
    private Label currentFileLabel;
    private TreeView<File> treeViewLeft;
    private TreeView<File> treeViewRight;
    private Button prevButton;
    private Button nextButton;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        // Carica configurazione
        loadConfig();
        
        // UI Setup
        BorderPane root = new BorderPane();
        
        // --- Menu Bar ---
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        // --- Split Pane (Left: Image, Right: Buttons) ---
        SplitPane splitPane = new SplitPane();
        
        // Left Side: Image View & Navigation
        VBox leftPane = createLeftPane();
        
        // Right Side: Destination Tree
        BorderPane rightPane = createRightPane();
        
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.7); // 70% image, 30% buttons
        
        root.setCenter(splitPane);
        
        // --- Status Bar ---
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
        
        // Initial Load
        refreshImages();
        refreshDestinationTree();
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
        menuBar.getMenus().add(fileMenu);
        
        return menuBar;
    }

    private VBox createLeftPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #2b2b2b;"); // Dark background for photos
        
        currentFileLabel = new Label("Nessuna immagine");
        currentFileLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(600);
        imageView.setFitWidth(800);
        
        // Navigation Buttons
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
        
        // Bind image view size to container
        imageView.fitWidthProperty().bind(box.widthProperty().subtract(20));
        imageView.fitHeightProperty().bind(box.heightProperty().subtract(100));
        
        return box;
    }

    private BorderPane createRightPane() {
        BorderPane rightPane = new BorderPane();
        
        Label title = new Label("Cartelle Destinazione:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10;");
        rightPane.setTop(title);
        
        // Create two TreeViews
        treeViewLeft = createConfiguredTreeView();
        treeViewRight = createConfiguredTreeView();
        
        // Sync selection: when one is selected, clear the other
        treeViewLeft.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) treeViewRight.getSelectionModel().clearSelection();
        });
        treeViewRight.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) treeViewLeft.getSelectionModel().clearSelection();
        });
        
        // SplitPane for two columns
        SplitPane splitPane = new SplitPane(treeViewLeft, treeViewRight);
        splitPane.setDividerPositions(0.5);
        
        rightPane.setCenter(splitPane);
        
        Button moveBtn = new Button("Sposta nella cartella selezionata");
        moveBtn.setMaxWidth(Double.MAX_VALUE);
        moveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
        moveBtn.setOnAction(e -> {
            TreeItem<File> selected = getSelectedFolder();
            if (selected != null) {
                moveCurrentImageTo(selected.getValue());
            } else {
                showError("Seleziona una cartella di destinazione");
            }
        });

//        Button deleteBtn = new Button("Elimina immagine corrente");
//        deleteBtn.setMaxWidth(Double.MAX_VALUE);
//        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
//        deleteBtn.setOnAction(e -> deleteCurrentImage());
//      
        VBox bottomBox = new VBox(10, moveBtn/* , deleteBtn*/);
        bottomBox.setPadding(new Insets(10));
        rightPane.setBottom(bottomBox);
        
        return rightPane;
    }
    
    private TreeView<File> createConfiguredTreeView() {
        TreeView<File> tv = new TreeView<>();
        tv.setShowRoot(false); // Hide the dummy root
        
        tv.setCellFactory(p -> new TreeCell<File>() {
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

    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        Properties props = new Properties();
        
        sourcePath = DEFAULT_SOURCE_PATH;
        destPath = DEFAULT_DEST_PATH;
        
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
                sourcePath = props.getProperty("source.path", DEFAULT_SOURCE_PATH);
                destPath = props.getProperty("dest.path", DEFAULT_DEST_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Save immediately to ensure file exists with defaults if it didn't exist
        if (!configFile.exists()) {
            saveConfig();
        }
    }

    private void saveConfig() {
        try {
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) configDir.mkdirs();
            
            Properties props = new Properties();
            props.setProperty("source.path", sourcePath);
            props.setProperty("dest.path", destPath);
            
            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                props.store(out, "AlNao Photo Dispatcher Configuration");
            }
        } catch (IOException e) {
            showError("Errore salvataggio configurazione: " + e.getMessage());
        }
    }

    private void showConfigDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Configurazione Cartelle");
        dialog.setHeaderText("Imposta le cartelle sorgente e destinazione");
        
        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField sourceField = new TextField(sourcePath);
        TextField destField = new TextField(destPath);
        
        Button sourceBtn = new Button("Sfoglia...");
        sourceBtn.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File(sourcePath).exists() ? new File(sourcePath) : null);
            File f = dc.showDialog(dialog.getOwner());
            if (f != null) sourceField.setText(f.getAbsolutePath() + "/");
        });
        
        Button destBtn = new Button("Sfoglia...");
        destBtn.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File(destPath).exists() ? new File(destPath) : null);
            File f = dc.showDialog(dialog.getOwner());
            if (f != null) destField.setText(f.getAbsolutePath() + "/");
        });
        
        grid.add(new Label("Sorgente:"), 0, 0);
        grid.add(sourceField, 1, 0);
        grid.add(sourceBtn, 2, 0);
        
        grid.add(new Label("Destinazione:"), 0, 1);
        grid.add(destField, 1, 1);
        grid.add(destBtn, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                sourcePath = sourceField.getText();
                destPath = destField.getText();
                saveConfig();
                refreshImages();
                refreshDestinationTree();
            }
            return null;
        });
        
        dialog.showAndWait();
    }

    private void refreshImages() {
        imageFiles.clear();
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
            // Ordina per nome
            Arrays.sort(files, Comparator.comparing(File::getName));
            imageFiles.addAll(Arrays.asList(files));
        }
        
        currentImageIndex = 0;
        showCurrentImage();
        statusLabel.setText("Trovate " + imageFiles.size() + " immagini in " + sourcePath);
    }

    private void refreshDestinationTree() {
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
            
            // Split into two columns
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
                // Recursively populate only if needed or do it all at once?
                // For "hundreds", doing it all at once is fine.
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
        
        // Bounds check
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

    private void moveCurrentImageTo(File targetFolder) {
        if (imageFiles.isEmpty()) return;
        
        File currentFile = imageFiles.get(currentImageIndex);
        File targetFile = new File(targetFolder, currentFile.getName());
        
        try {
            // Se esiste già, rinomina o gestisci (qui aggiungo timestamp per semplicità)
            if (targetFile.exists()) {
                String name = currentFile.getName();
                String ext = "";
                int dot = name.lastIndexOf('.');
                if (dot > 0) {
                    ext = name.substring(dot);
                    name = name.substring(0, dot);
                }
                targetFile = new File(targetFolder, name + "_" + System.currentTimeMillis() + ext);
            }
            
            Files.move(currentFile.toPath(), targetFile.toPath());
            
            statusLabel.setText("Spostato: " + currentFile.getName() + " -> " + targetFolder.getName());
            
            // Rimuovi dalla lista e aggiorna vista
            imageFiles.remove(currentImageIndex);
            
            // Se eravamo all'ultima, l'indice ora punta fuori, quindi decrementa
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
                
                // Rimuovi dalla lista e aggiorna vista
                imageFiles.remove(currentImageIndex);
                
                // Se eravamo all'ultima, l'indice ora punta fuori, quindi decrementa
                if (currentImageIndex >= imageFiles.size()) {
                    currentImageIndex = Math.max(0, imageFiles.size() - 1);
                }
                
                showCurrentImage();
                
            } catch (IOException e) {
                showError("Errore durante l'eliminazione: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}