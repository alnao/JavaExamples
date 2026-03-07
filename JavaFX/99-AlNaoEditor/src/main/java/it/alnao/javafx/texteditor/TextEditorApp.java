package it.alnao.javafx.texteditor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Progetto 05 - Simple Text Editor
 * Concetto principale: MenuBar e FileChooser
 * 
 * Editor di testo con menu File (Nuovo, Apri, Salva, Esci)
 * e operazioni di File I/O.
 */
public class TextEditorApp extends Application {
    
    private TextArea textArea;
    private Stage primaryStage;
    private ComboBox<FileTab> fileSelector;
    private List<FileTab> openFiles;
    private FileTab currentFileTab;
    
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.alnaoEditor";
    private static final String WINDOW_CONFIG_FILE = CONFIG_DIR + "/window.txt";
    private static final String FILE_LIST_FILE = CONFIG_DIR + "/fileList.txt";
    
    /**
     * Classe interna per gestire i file aperti
     */
    private static class FileTab {
        private File file;
        private String content;
        private boolean isModified;
        
        public FileTab(File file, String content) {
            this.file = file;
            this.content = content;
            this.isModified = false;
        }
        
        public File getFile() { return file; }
        public String getContent() { return content; }
        public void setContent(String content) { 
            this.content = content;
            this.isModified = true;
        }
        public boolean isModified() { return isModified; }
        public void setModified(boolean modified) { this.isModified = modified; }
        
        @Override
        public String toString() {
            String name = file != null ? file.getName() : "Untitled";
            String path = file != null ? " (" + file.getAbsolutePath() + ")" : "";
            String modified = isModified ? " *" : "";
            return name + path + modified;
        }
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.openFiles = new ArrayList<>();

        // TextArea principale
        textArea = new TextArea();
        textArea.setStyle(
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 14px; " +
            "-fx-background-color: white;"
        );
        
        // Listener per salvare le modifiche nel FileTab corrente
        textArea.textProperty().addListener((obs, oldText, newText) -> {
            if (currentFileTab != null && !newText.equals(currentFileTab.getContent())) {
                currentFileTab.setContent(newText);
                updateFileSelector();
            }
        });

        // MenuBar con bottone Salva Tutti
        MenuBar menuBar = createMenuBar();
        
        // Bottone Salva Tutti
        Button saveAllButton = new Button("💾 Salva tutti");
        saveAllButton.setStyle("-fx-font-size: 12px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
        saveAllButton.setOnAction(e -> saveAllFiles());
        
        // HBox per MenuBar e Bottone
        HBox menuBox = new HBox(menuBar, saveAllButton);
        menuBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        menuBox.setStyle("-fx-background-color: white; -fx-border-color: #ced4da; -fx-border-width: 0 0 1 0;");
        
        // ComboBox per selezionare i file
        fileSelector = new ComboBox<>();
        fileSelector.setMaxWidth(Double.MAX_VALUE);
        fileSelector.setStyle("-fx-font-size: 12px;");
        fileSelector.setOnAction(e -> switchToSelectedFile());
        HBox.setHgrow(fileSelector, Priority.ALWAYS);
        
        HBox selectorBox = new HBox(fileSelector);
        selectorBox.setStyle("-fx-background-color: white; -fx-border-color: #ced4da; -fx-border-width: 0 0 1 0;");
        selectorBox.setPadding(new Insets(5));

        // Layout
        VBox topBox = new VBox(menuBox, selectorBox);
        topBox.setSpacing(0);
        
        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(textArea);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("AlNao Editor");
        primaryStage.setScene(scene);
        
        // Carica le impostazioni della finestra salvate
        loadWindowSettings();
        
        // Carica la lista dei file salvati
        loadFileList();
        
        // Se non ci sono file aperti, crea un nuovo file
        if (openFiles.isEmpty()) {
            createNewFile();
        }
        
        // Salva le impostazioni quando la finestra viene chiusa
        primaryStage.setOnCloseRequest(e -> {
            // Verifica se ci sono file modificati
            boolean hasModified = openFiles.stream().anyMatch(FileTab::isModified);
            
            if (hasModified) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Chiusura applicazione");
                confirm.setHeaderText("Ci sono file modificati non salvati.");
                confirm.setContentText("Vuoi salvare le modifiche prima di chiudere?");
                
                ButtonType saveAll = new ButtonType("Salva tutto");
                ButtonType closeWithoutSaving = new ButtonType("Chiudi senza salvare");
                ButtonType cancel = new ButtonType("Non chiudere", ButtonBar.ButtonData.CANCEL_CLOSE);
                
                confirm.getButtonTypes().setAll(saveAll, closeWithoutSaving, cancel);
                
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == saveAll) {
                        saveAllFiles();
                    } else if (result.get() == cancel) {
                        e.consume(); // Annulla la chiusura
                        return;
                    }
                    // Se closeWithoutSaving, continua senza salvare
                } else {
                    e.consume(); // Se chiude il dialog, annulla la chiusura
                    return;
                }
            }
            
            saveWindowSettings();
            saveFileList();
        });
        
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
        
        MenuItem openFolderItem = new MenuItem("Apri tutti i file di una cartella...");
        openFolderItem.setOnAction(e -> openAllFilesFromFolder());
        
        MenuItem closeItem = new MenuItem("Chiudi file corrente");
        closeItem.setOnAction(e -> closeCurrentFile());
        
        MenuItem saveItem = new MenuItem("Salva");
        saveItem.setOnAction(e -> saveFile());
        
        MenuItem saveAsItem = new MenuItem("Salva con nome...");
        saveAsItem.setOnAction(e -> saveFileAs());
        
        MenuItem saveAllItem = new MenuItem("Salva tutti");
        saveAllItem.setOnAction(e -> saveAllFiles());
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        
        MenuItem exitItem = new MenuItem("Esci");
        exitItem.setOnAction(e -> exitApp());
        
        fileMenu.getItems().addAll(newItem, openItem, openFolderItem, closeItem, new SeparatorMenuItem(), 
                                    saveItem, saveAsItem, saveAllItem, separator, exitItem);

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
     * Crea un nuovo file vuoto
     */
    private void createNewFile() {
        FileTab newTab = new FileTab(null, "");
        openFiles.add(newTab);
        currentFileTab = newTab;
        textArea.setText("");
        updateFileSelector();
        fileSelector.getSelectionModel().select(newTab);
    }
    
    /**
     * Crea un nuovo file
     */
    private void newFile() {
        createNewFile();
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
            // Verifica se il file è già aperto
            for (FileTab tab : openFiles) {
                if (tab.getFile() != null && tab.getFile().equals(file)) {
                    currentFileTab = tab;
                    textArea.setText(tab.getContent());
                    fileSelector.getSelectionModel().select(tab);
                    return;
                }
            }
            
            // Apri il nuovo file
            try {
                String content = Files.readString(file.toPath());
                FileTab newTab = new FileTab(file, content);
                openFiles.add(newTab);
                currentFileTab = newTab;
                textArea.setText(content);
                updateFileSelector();
                fileSelector.getSelectionModel().select(newTab);
            } catch (IOException e) {
                showError("Errore durante l'apertura del file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Apre tutti i file di testo da una cartella selezionata
     */
    private void openAllFilesFromFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleziona una cartella");
        
        File directory = directoryChooser.showDialog(primaryStage);
        if (directory != null && directory.isDirectory()) {
            try (Stream<Path> paths = Files.walk(directory.toPath(), 1)) {
                List<File> files = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase();
                        return name.endsWith(".txt") || name.endsWith(".java") || 
                               name.endsWith(".xml") || name.endsWith(".md") ||
                               name.endsWith(".json") || name.endsWith(".properties") ||
                               name.endsWith(".yml") || name.endsWith(".yaml");
                    })
                    .map(Path::toFile)
                    .toList();
                
                if (files.isEmpty()) {
                    showInfo("Nessun file di testo trovato nella cartella selezionata.");
                    return;
                }
                
                int openedCount = 0;
                int skippedCount = 0;
                
                for (File file : files) {
                    // Verifica se il file è già aperto
                    boolean alreadyOpen = false;
                    for (FileTab tab : openFiles) {
                        if (tab.getFile() != null && tab.getFile().equals(file)) {
                            alreadyOpen = true;
                            skippedCount++;
                            break;
                        }
                    }
                    
                    if (!alreadyOpen) {
                        try {
                            String content = Files.readString(file.toPath());
                            FileTab newTab = new FileTab(file, content);
                            openFiles.add(newTab);
                            openedCount++;
                            
                            // Imposta il primo file come corrente
                            if (currentFileTab == null || openedCount == 1) {
                                currentFileTab = newTab;
                                textArea.setText(content);
                            }
                        } catch (IOException e) {
                            System.err.println("Errore durante l'apertura del file " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
                
                updateFileSelector();
                if (currentFileTab != null) {
                    fileSelector.getSelectionModel().select(currentFileTab);
                }
                
                String message = String.format("Aperti %d file.", openedCount);
                if (skippedCount > 0) {
                    message += String.format(" (%d già aperti saltati)", skippedCount);
                }
                showInfo(message);
                
            } catch (IOException e) {
                showError("Errore durante la lettura della cartella: " + e.getMessage());
            }
        }
    }
    
    /**
     * Chiude il file corrente
     */
    private void closeCurrentFile() {
        if (currentFileTab == null) return;
        
        if (currentFileTab.isModified()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Chiudi File");
            confirm.setHeaderText("Il file è stato modificato. Vuoi salvarlo?");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.YES) {
                    saveFile();
                } else if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }
        
        openFiles.remove(currentFileTab);
        
        if (openFiles.isEmpty()) {
            createNewFile();
        } else {
            currentFileTab = openFiles.get(0);
            textArea.setText(currentFileTab.getContent());
            updateFileSelector();
            fileSelector.getSelectionModel().select(currentFileTab);
        }
    }
    
    /**
     * Cambia al file selezionato nel ComboBox
     */
    private void switchToSelectedFile() {
        FileTab selected = fileSelector.getSelectionModel().getSelectedItem();
        if (selected != null && selected != currentFileTab) {
            currentFileTab = selected;
            textArea.setText(currentFileTab.getContent());
        }
    }
    
    /**
     * Aggiorna il ComboBox con la lista dei file ordinati alfabeticamente
     */
    private void updateFileSelector() {
        fileSelector.getItems().clear();
        
        // Ordina i file alfabeticamente per nome
        List<FileTab> sortedFiles = new ArrayList<>(openFiles);
        sortedFiles.sort((a, b) -> {
            String nameA = a.getFile() != null ? a.getFile().getName() : "Untitled";
            String nameB = b.getFile() != null ? b.getFile().getName() : "Untitled";
            return nameA.compareToIgnoreCase(nameB);
        });
        
        fileSelector.getItems().addAll(sortedFiles);
        if (currentFileTab != null) {
            fileSelector.getSelectionModel().select(currentFileTab);
        }
    }

    /**
     * Salva il file corrente
     */
    private void saveFile() {
        if (currentFileTab == null) return;
        
        if (currentFileTab.getFile() == null) {
            saveFileAs();
        } else {
            try {
                Files.writeString(currentFileTab.getFile().toPath(), currentFileTab.getContent());
                currentFileTab.setModified(false);
                updateFileSelector();
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
        if (currentFileTab == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva con Nome");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File di Testo", "*.txt")
        );
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                Files.writeString(file.toPath(), currentFileTab.getContent());
                
                // Se era un file senza nome, aggiorna il riferimento
                if (currentFileTab.getFile() == null) {
                    currentFileTab = new FileTab(file, currentFileTab.getContent());
                    int index = openFiles.indexOf(fileSelector.getSelectionModel().getSelectedItem());
                    openFiles.set(index, currentFileTab);
                } else {
                    currentFileTab = new FileTab(file, currentFileTab.getContent());
                }
                
                currentFileTab.setModified(false);
                updateFileSelector();
                fileSelector.getSelectionModel().select(currentFileTab);
                showInfo("File salvato con successo!");
            } catch (IOException e) {
                showError("Errore durante il salvataggio: " + e.getMessage());
            }
        }
    }
    
    /**
     * Salva tutti i file aperti
     */
    private void saveAllFiles() {
        for (FileTab tab : openFiles) {
            if (tab.getFile() != null && tab.isModified()) {
                try {
                    Files.writeString(tab.getFile().toPath(), tab.getContent());
                    tab.setModified(false);
                } catch (IOException e) {
                    showError("Errore durante il salvataggio di " + tab.getFile().getName() + ": " + e.getMessage());
                }
            }
        }
        updateFileSelector();
        //showInfo("Tutti i file salvati con successo!");
    }

    /**
     * Chiude l'applicazione
     */
    private void exitApp() {
        // Verifica se ci sono file modificati
        boolean hasModified = openFiles.stream().anyMatch(FileTab::isModified);
        
        if (hasModified) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Esci");
            confirm.setHeaderText("Ci sono file modificati. Vuoi salvarli prima di uscire?");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.YES) {
                    saveAllFiles();
                } else if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }
        
        saveWindowSettings();
        saveFileList();
        primaryStage.close();
    }

    /**
     * Mostra info sull'applicazione
     */
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Simple AlNaoEditor v1.0");
        alert.setContentText("Progetto JavaFX 99 - AlNaoEditor\n\n" +
                           "Editor di testo semplice con:\n" +
                           "- Apertura e salvataggio file\n" +
                           "- Operazioni di editing base\n" +
                           "- Gestione di più file aperti\n" +
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

    /**
     * Salva le impostazioni della finestra (dimensioni e posizione)
     */
    private void saveWindowSettings() {
        try {
            // Crea la directory se non esiste
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            Properties properties = new Properties();
            properties.setProperty("window.width", String.valueOf(primaryStage.getWidth()));
            properties.setProperty("window.height", String.valueOf(primaryStage.getHeight()));
            properties.setProperty("window.x", String.valueOf(primaryStage.getX()));
            properties.setProperty("window.y", String.valueOf(primaryStage.getY()));
            
            try (FileOutputStream out = new FileOutputStream(WINDOW_CONFIG_FILE)) {
                properties.store(out, "AlNao Editor Window Settings");
            }
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio delle impostazioni della finestra: " + e.getMessage());
        }
    }

    /**
     * Carica le impostazioni della finestra salvate
     */
    private void loadWindowSettings() {
        File configFile = new File(WINDOW_CONFIG_FILE);
        if (!configFile.exists()) {
            return; // File non esistente, usa le impostazioni di default
        }
        
        try (FileInputStream in = new FileInputStream(configFile)) {
            Properties properties = new Properties();
            properties.load(in);
            
            String width = properties.getProperty("window.width");
            String height = properties.getProperty("window.height");
            String x = properties.getProperty("window.x");
            String y = properties.getProperty("window.y");
            
            if (width != null && height != null) {
                primaryStage.setWidth(Double.parseDouble(width));
                primaryStage.setHeight(Double.parseDouble(height));
            }
            
            if (x != null && y != null) {
                primaryStage.setX(Double.parseDouble(x));
                primaryStage.setY(Double.parseDouble(y));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Errore durante il caricamento delle impostazioni della finestra: " + e.getMessage());
        }
    }
    
    /**
     * Salva la lista dei file aperti
     */
    private void saveFileList() {
        try {
            // Crea la directory se non esiste
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_LIST_FILE))) {
                for (FileTab tab : openFiles) {
                    if (tab.getFile() != null) {
                        writer.write(tab.getFile().getAbsolutePath());
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio della lista file: " + e.getMessage());
        }
    }
    
    /**
     * Carica la lista dei file salvati
     */
    private void loadFileList() {
        File fileListFile = new File(FILE_LIST_FILE);
        if (!fileListFile.exists()) {
            return; // File non esistente
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileListFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                File file = new File(line);
                if (file.exists() && file.isFile()) {
                    try {
                        String content = Files.readString(file.toPath());
                        FileTab tab = new FileTab(file, content);
                        openFiles.add(tab);
                    } catch (IOException e) {
                        System.err.println("Errore durante il caricamento del file " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
            
            if (!openFiles.isEmpty()) {
                currentFileTab = openFiles.get(0);
                textArea.setText(currentFileTab.getContent());
                updateFileSelector();
                fileSelector.getSelectionModel().select(currentFileTab);
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento della lista file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
