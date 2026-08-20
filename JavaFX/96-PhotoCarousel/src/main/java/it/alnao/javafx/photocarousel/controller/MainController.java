package it.alnao.javafx.photocarousel.controller;

import it.alnao.javafx.photocarousel.service.ConfigService;
import it.alnao.javafx.photocarousel.service.ImageService;
import it.alnao.javafx.photocarousel.view.FolderConfigDialog;
import it.alnao.javafx.photocarousel.view.PhotoCard;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Controller for Photo Carousel JavaFX Application.
 */
public class MainController {

    private final ConfigService configService;
    private final ImageService imageService;
    private final Stage primaryStage;

    private BorderPane rootLayout;
    private ComboBox<String> folderComboBox;
    private Button playButton;
    private Button stopButton;
    private Spinner<Integer> secondsSpinner;
    private Label statusLabel;
    private HBox carouselBox;
    private ScrollPane scrollPane;

    private Timeline playTimeline;
    private boolean isPlaying = false;
    private int currentN = 4;
    private double currentCardWidth = 300;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.configService = new ConfigService();
        this.imageService = new ImageService();
    }

    private VBox topContainer;

    public Pane buildUI() {
        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #11111b; -fx-font-family: 'Segoe UI', sans-serif;");

        // Top toolbar
        topContainer = createTopToolbar();
        rootLayout.setTop(topContainer);

        // Center carousel container without padding/margins
        carouselBox = new HBox();
        carouselBox.setAlignment(Pos.CENTER);
        carouselBox.setPadding(Insets.EMPTY);
        carouselBox.setStyle("-fx-background-color: transparent;");

        StackPane centerPane = new StackPane(carouselBox);
        centerPane.setAlignment(Pos.CENTER);

        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(centerPane.widthProperty());
        clipRect.heightProperty().bind(centerPane.heightProperty());
        centerPane.setClip(clipRect);

        scrollPane = new ScrollPane(centerPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPadding(Insets.EMPTY);
        scrollPane.setStyle("-fx-background: #11111b; -fx-background-color: #11111b; -fx-viewport-border: none;");

        rootLayout.setCenter(scrollPane);

        // Responsive resize listener
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> recalculateLayoutAndReload(newVal.doubleValue()));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> recalculateLayoutAndReload(primaryStage.getWidth()));
        scrollPane.heightProperty().addListener((obs, oldVal, newVal) -> recalculateLayoutAndReload(primaryStage.getWidth()));

        // Initialize folder list
        refreshFoldersDropdown();

        return rootLayout;
    }

    private double calculateMaxBodyHeight() {
        double stageHeight = primaryStage.getHeight();
        if (stageHeight <= 0) stageHeight = 900;

        double topHeight = (topContainer != null && topContainer.getHeight() > 0) ? topContainer.getHeight() : 90;
        double available = stageHeight - topHeight;

        return Math.max(100, available - 10);
    }

    private VBox createTopToolbar() {
        VBox container = new VBox(5);
        container.setPadding(new Insets(12, 20, 12, 20));
        container.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 2px 0;");

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // App title badge
        Label titleLabel = new Label("📷 Photo Carousel");
        titleLabel.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Folder selection dropdown
        folderComboBox = new ComboBox<>();
        folderComboBox.setPromptText("Seleziona Cartella Immagini...");
        folderComboBox.setPrefWidth(280);
        folderComboBox.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 13px; -fx-background-radius: 6px;");

        folderComboBox.setOnAction(e -> {
            String selected = folderComboBox.getValue();
            if (selected != null) {
                onFolderSelected(selected);
            }
        });

        // Config dialog button
        Button btnConfig = new Button("⚙️ Cartelle");
        btnConfig.setTooltip(new Tooltip("Gestisci lista cartelle configurate"));
        btnConfig.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnConfig.setOnAction(e -> openFolderConfigDialog());

        // Play / Stop controls
        playButton = new Button("▶ Play");
        playButton.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 14px;");
        playButton.setOnAction(e -> startCarousel());

        stopButton = new Button("⏹ Stop");
        stopButton.setDisable(true);
        stopButton.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 14px;");
        stopButton.setOnAction(e -> stopCarousel());

        // Interval S seconds input
        Label intervalLabel = new Label("Intervallo S (sec):");
        intervalLabel.setStyle("-fx-text-fill: #bac2de; -fx-font-size: 13px;");

        secondsSpinner = new Spinner<>(1, 300, configService.getIntervalSeconds());
        secondsSpinner.setEditable(true);
        secondsSpinner.setPrefWidth(80);
        secondsSpinner.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 6px;");

        secondsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal > 0) {
                configService.setIntervalSeconds(newVal);
                if (isPlaying) {
                    // Restart timer with new interval
                    startCarousel();
                }
            }
        });

        // 10% Overlap Flag CheckBox (default false / disabled)
        CheckBox cbOverlap = new CheckBox("Sovrapposizione 10%");
        cbOverlap.setSelected(configService.isOverlap10());
        cbOverlap.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 13px; -fx-cursor: hand;");
        cbOverlap.setTooltip(new Tooltip("Attiva layout compatto con sovrapposizione foto 10%"));
        cbOverlap.selectedProperty().addListener((obs, oldVal, newVal) -> {
            configService.setOverlap10(newVal);
            recalculateLayoutAndReload(primaryStage.getWidth());
        });

        // Center Large Flag CheckBox (default true / enabled)
        CheckBox cbCenterLarge = new CheckBox("Centrale grande");
        cbCenterLarge.setSelected(configService.isCenterLarge());
        cbCenterLarge.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 13px; -fx-cursor: hand;");
        cbCenterLarge.setTooltip(new Tooltip("Mostra al massimo 3 foto con quella centrale doppia"));
        cbCenterLarge.selectedProperty().addListener((obs, oldVal, newVal) -> {
            configService.setCenterLarge(newVal);
            recalculateLayoutAndReload(primaryStage.getWidth());
        });

        // Allow Vertical Overflow Flag CheckBox (default false / disabled)
        CheckBox cbAllowOverflow = new CheckBox("Permetti sbordo");
        cbAllowOverflow.setSelected(configService.isAllowOverflow());
        cbAllowOverflow.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 13px; -fx-cursor: hand;");
        cbAllowOverflow.setTooltip(new Tooltip("Permetti alla foto centrale di sbordare verticalmente in modo centrato"));
        cbAllowOverflow.selectedProperty().addListener((obs, oldVal, newVal) -> {
            configService.setAllowOverflow(newVal);
            recalculateLayoutAndReload(primaryStage.getWidth());
        });

        // Include Subfolders Flag CheckBox (default true / enabled)
        CheckBox cbIncludeSubfolders = new CheckBox("Includi sottocartelle");
        cbIncludeSubfolders.setSelected(configService.isIncludeSubfolders());
        cbIncludeSubfolders.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 13px; -fx-cursor: hand;");
        cbIncludeSubfolders.setTooltip(new Tooltip("Scansiona ricorsivamente tutte le sottocartelle per le foto"));
        cbIncludeSubfolders.selectedProperty().addListener((obs, oldVal, newVal) -> {
            configService.setIncludeSubfolders(newVal);
            String selected = folderComboBox.getValue();
            if (selected != null) {
                onFolderSelected(selected);
            }
        });

        // Fullscreen toggle button
        Button btnFullscreen = new Button("⛶ Schermo Intero");
        btnFullscreen.setStyle("-fx-background-color: #585b70; -fx-text-fill: #cdd6f4; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnFullscreen.setOnAction(e -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));

        toolbar.getChildren().addAll(
                titleLabel,
                folderComboBox,
                btnConfig,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                playButton,
                stopButton,
                intervalLabel,
                secondsSpinner,
                cbOverlap,
                cbCenterLarge,
                cbAllowOverflow,
                cbIncludeSubfolders,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                btnFullscreen
        );

        statusLabel = new Label("Pronto. Seleziona una cartella per iniziare.");
        statusLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px; -fx-font-style: italic;");

        container.getChildren().addAll(toolbar, statusLabel);
        return container;
    }

    private void refreshFoldersDropdown() {
        List<String> folders = configService.getFolders();
        if (folders != null) {
            folders.sort(String.CASE_INSENSITIVE_ORDER);
        }
        folderComboBox.setItems(FXCollections.observableArrayList(folders != null ? folders : new ArrayList<>()));
        if (folders != null && !folders.isEmpty()) {
            folderComboBox.getSelectionModel().selectFirst();
        } else {
            statusLabel.setText("Nessuna cartella configurata. Clicca su '⚙️ Cartelle' per aggiungerne una.");
        }
    }

    private void openFolderConfigDialog() {
        FolderConfigDialog dialog = new FolderConfigDialog(primaryStage, configService.getFolders());
        if (dialog.showAndWait()) {
            configService.setFolders(dialog.getFolders());
            refreshFoldersDropdown();
        }
    }

    private void onFolderSelected(String folderPath) {
        stopCarousel();
        List<File> images = imageService.loadImagesFromFolder(folderPath, configService.isIncludeSubfolders());

        if (images.isEmpty()) {
            carouselBox.getChildren().clear();
            statusLabel.setText("⚠️ Nessuna immagine valida trovata nella cartella: " + folderPath);
            return;
        }

        statusLabel.setText("Cartella selezionata: " + folderPath + " (" + images.size() + " immagini disponibili)");
        recalculateLayoutAndReload(primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 1200);
    }

    private void recalculateLayoutAndReload(double windowWidth) {
        if (windowWidth <= 0) return;

        boolean overlap10 = configService.isOverlap10();
        boolean centerLarge = configService.isCenterLarge();

        int maxAllowedN = centerLarge ? 3 : 4;
        int calculatedN = (int) Math.floor(windowWidth / 300.0);
        currentN = Math.min(maxAllowedN, Math.max(1, calculatedN));

        if (overlap10) {
            // Mode 10% overlap: max width, zero padding, negative spacing
            if (centerLarge && currentN == 3) {
                currentCardWidth = Math.max(300, windowWidth / 3.7);
            } else {
                double widthFactor = 1.0 + (currentN - 1) * 0.90;
                currentCardWidth = Math.max(300, windowWidth / widthFactor);
            }

            carouselBox.setSpacing(-0.10 * currentCardWidth);
            carouselBox.setPadding(Insets.EMPTY);
        } else {
            // Default mode: original large photos with padding & margins
            double spacing = 20;
            double availableWidth = Math.max(400, windowWidth - 100);

            if (centerLarge && currentN == 3) {
                currentCardWidth = Math.max(300, (availableWidth - 40) / 4.0);
            } else {
                double calculatedWidth = (availableWidth - (spacing * (currentN + 1))) / currentN;
                currentCardWidth = Math.max(300, Math.min(windowWidth / 4.0, calculatedWidth));
            }

            carouselBox.setSpacing(20);
            carouselBox.setPadding(new Insets(30));
        }

        reloadCarouselCards();
    }

    private void reloadCarouselCards() {
        if (imageService.getFolderImageCount() == 0) return;

        boolean overlap10 = configService.isOverlap10();
        boolean centerLarge = configService.isCenterLarge();
        boolean allowOverflow = configService.isAllowOverflow();
        double maxH = calculateMaxBodyHeight();

        carouselBox.getChildren().clear();
        carouselBox.setSpacing(overlap10 ? -0.10 * currentCardWidth : 20);
        carouselBox.setPadding(overlap10 ? Insets.EMPTY : new Insets(30));

        List<File> initialFiles = imageService.getInitialImages(currentN);
        for (int i = 0; i < initialFiles.size(); i++) {
            File f = initialFiles.get(i);
            double cardW = (centerLarge && currentN == 3 && i == 1) ? (currentCardWidth * 2.0) : currentCardWidth;
            PhotoCard card = new PhotoCard(f, cardW, maxH, overlap10, allowOverflow);
            carouselBox.getChildren().add(card);
        }

        String modeText = (overlap10 ? "sovrapposizione 10%" : "con margini") + (centerLarge ? ", centrale doppia" : "") + (allowOverflow ? ", sbordo centrato" : "");
        statusLabel.setText("Visualizzazione di " + carouselBox.getChildren().size() + " foto (N max=" + currentN + ", larghezza base=" + (int)currentCardWidth + "px, " + modeText + ")");
    }

    private List<File> getCurrentlyDisplayedFiles() {
        List<File> list = new ArrayList<>();
        for (Node node : carouselBox.getChildren()) {
            if (node instanceof PhotoCard card) {
                list.add(card.getImageFile());
            }
        }
        return list;
    }

    private void startCarousel() {
        if (imageService.getFolderImageCount() == 0) {
            statusLabel.setText("⚠️ Impossibile avviare: Nessuna immagine disponibile.");
            return;
        }

        stopCarousel();

        isPlaying = true;
        playButton.setDisable(true);
        stopButton.setDisable(false);
        statusLabel.setText("▶ Carousel ATTIVO (Cambio foto ogni " + configService.getIntervalSeconds() + " secondi)");

        int seconds = configService.getIntervalSeconds();
        playTimeline = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> rotateCarouselStep()));
        playTimeline.setCycleCount(Timeline.INDEFINITE);
        playTimeline.play();
    }

    private void stopCarousel() {
        isPlaying = false;
        if (playTimeline != null) {
            playTimeline.stop();
            playTimeline = null;
        }
        playButton.setDisable(false);
        stopButton.setDisable(true);
        statusLabel.setText("⏹ Carousel fermato.");
    }

    private void rotateCarouselStep() {
        if (!isPlaying || carouselBox.getChildren().isEmpty()) return;

        List<File> currentFiles = getCurrentlyDisplayedFiles();
        File nextFile = imageService.getRandomNextImage(currentFiles);
        if (nextFile == null) return;

        boolean overlap10 = configService.isOverlap10();
        boolean centerLarge = configService.isCenterLarge();
        boolean allowOverflow = configService.isAllowOverflow();
        double maxH = calculateMaxBodyHeight();

        // Remove first card (index 0)
        carouselBox.getChildren().remove(0);

        // Add new card at end
        PhotoCard newCard = new PhotoCard(nextFile, currentCardWidth, maxH, overlap10, allowOverflow);
        carouselBox.getChildren().add(newCard);

        // Re-apply target widths & max height according to new positions (index 1 is center)
        for (int i = 0; i < carouselBox.getChildren().size(); i++) {
            Node node = carouselBox.getChildren().get(i);
            if (node instanceof PhotoCard card) {
                double cardW = (centerLarge && currentN == 3 && i == 1) ? (currentCardWidth * 2.0) : currentCardWidth;
                card.updateTargetSize(cardW, maxH, allowOverflow);
            }
        }
    }

    public void cleanup() {
        stopCarousel();
    }
}
