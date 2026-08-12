package it.alnao.javafx.controlroom.controller;

import it.alnao.javafx.controlroom.model.MonitorEntry;
import it.alnao.javafx.controlroom.model.ScriptEntry;
import it.alnao.javafx.controlroom.model.TabConfig;
import it.alnao.javafx.controlroom.service.ConfigService;
import it.alnao.javafx.controlroom.service.ScriptRunner;
import it.alnao.javafx.controlroom.service.StatusChecker;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URI;
import java.util.*;

/**
 * Main controller that builds the Control Room UI:
 * - Header bar with status indicators (red/green circles)
 * - TabPane where each tab has script buttons + output textarea + stop button
 */
public class ControlRoomController {

    // -- Bootstrap-like palette --
    private static final String TEXT_SECONDARY = "#6c757d";
    private static final String TEXT_SUCCESS = "#198754";
    private static final String YELLOW_WAIT = "#ffc107";

    private final ConfigService configService = new ConfigService();
    private StatusChecker statusChecker;

    // Map indicator boxes by monitor index for background color updates
    private final Map<Integer, HBox> indicatorBoxes = new LinkedHashMap<>();
    private final Map<Integer, Label> indicatorLabels = new LinkedHashMap<>();
    // Map tab index -> ScriptRunner
    private final Map<Integer, ScriptRunner> tabRunners = new LinkedHashMap<>();
    // Map tab index -> Tab node (to update tab header style)
    private final Map<Integer, Tab> indicatorTabs = new LinkedHashMap<>();
    // Warning label shown in header when any script is running
    private Label runningWarningLabel;

    public void start(Stage stage) {
        configService.load();

        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("control-room-root");

        // --- Header ---
        HBox header = buildHeader();
        root.setTop(header);

        // --- Tabs ---
        TabPane tabPane = buildTabPane();
        root.setCenter(tabPane);

        // --- Footer ---
        HBox footer = buildFooter();
        root.setBottom(footer);

        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(
            getClass().getResource("/it/alnao/javafx/controlroom/control-room-bootstrap.css")
        ).toExternalForm());

        stage.setTitle("AlNao Sh Control Room");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();

        // Start monitoring
        startStatusChecker();

        // Cleanup on close
        stage.setOnCloseRequest(e -> shutdown());
    }

    // ====================== HEADER ======================

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.getStyleClass().add("header-bar");

        // Running warning label (hidden by default)
        runningWarningLabel = new Label("Something is running");
        runningWarningLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        runningWarningLabel.getStyleClass().add("warning-pill");
        runningWarningLabel.setVisible(false);
        runningWarningLabel.setManaged(false);

        header.getChildren().add(runningWarningLabel);

        // Indicators
        for (MonitorEntry monitor : configService.getMonitors()) {
            HBox indicator = buildIndicator(monitor);
            header.getChildren().add(indicator);
        }

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        // Settings button
        Button settingsBtn = new Button("⚙");
        styleSmallButton(settingsBtn);
        settingsBtn.setTooltip(new Tooltip("Settings"));
        settingsBtn.setOnAction(e -> {
            SettingsController settingsController = new SettingsController(configService);
            settingsController.showSettingsWindow();
        });
        header.getChildren().add(settingsBtn);

        // Refresh button
        Button refreshBtn = new Button("🔄");
        styleSmallButton(refreshBtn);
        refreshBtn.setTooltip(new Tooltip("Refresh"));
        refreshBtn.setOnAction(e -> {
            // Set all to yellow (checking)
            indicatorBoxes.values().forEach(b -> applyIndicatorStyle(b, YELLOW_WAIT));
            if (statusChecker != null) {
                new Thread(() -> statusChecker.checkAll(), "manual-refresh").start();
            }
        });
        header.getChildren().add(refreshBtn);

        return header;
    }

    private HBox buildIndicator(MonitorEntry monitor) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(4, 10, 4, 10));
        box.getStyleClass().add("indicator-chip");


        Label label = new Label(monitor.label());
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
    label.getStyleClass().add("indicator-label");

        // Tooltip with URL
        Tooltip tip = new Tooltip(monitor.url() + " (Click to open if green)");
        tip.setStyle("-fx-font-size: 11;");
        Tooltip.install(box, tip);

        // Store the box (not a circle) for background updates
        indicatorBoxes.put(monitor.index(), box);
        indicatorLabels.put(monitor.index(), label);

        // Initial state: yellow (checking)
        applyIndicatorStyle(box, YELLOW_WAIT);

        box.getChildren().add(label);

        box.setOnMouseClicked(e -> {
            Boolean isAlive = (Boolean) box.getProperties().get("isAlive");
            if (Boolean.TRUE.equals(isAlive)) {
                openInBrowser(monitor.url());
            }
        });

        // --- Play button: removed as requested ---
        // --- Stop button: removed as requested ---

        return box;
    }

    /**
     * Opens a URL in the system default browser.
     */
    private void openInBrowser(String urlStr) {
        try {
            String full = urlStr;
            if (!full.startsWith("http://") && !full.startsWith("https://")) {
                full = "http://" + full;
            }
            // Use xdg-open on Linux as Desktop.browse may not work on all setups
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("linux")) {
                new ProcessBuilder("xdg-open", full).start();
            } else {
                java.awt.Desktop.getDesktop().browse(URI.create(full));
            }
        } catch (Exception ex) {
            System.err.println("[ControlRoom] Failed to open browser: " + ex.getMessage());
        }
    }

    /**
     * Kills the process listening on the given port using `fuser -k`.
     */
    /*
    private void killProcessOnPort(int port) {
        new Thread(() -> {
            try {
                System.out.println("[ControlRoom] Killing process on port " + port + "...");
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", "fuser -k " + port + "/tcp 2>&1");
                pb.redirectErrorStream(true);
                Process proc = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[fuser] " + line);
                    }
                }
                int exitCode = proc.waitFor();
                System.out.println("[ControlRoom] fuser exited with code " + exitCode);
            } catch (Exception ex) {
                System.err.println("[ControlRoom] Failed to kill port " + port + ": " + ex.getMessage());
            }
        }, "kill-port-" + port).start();
    }
    */

    // ====================== TAB PANE ======================

    private TabPane buildTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("tab-pane-bs");
        tabPane.setPadding(new Insets(8));

        List<TabConfig> tabs = configService.getTabs();

        if (tabs.isEmpty()) {
            // Show a placeholder tab
            Tab emptyTab = new Tab("No Tabs Configured");
            Label placeholder = new Label("Configure tabs in the .env file");
            placeholder.setTextFill(Color.web(TEXT_SECONDARY));
            placeholder.setFont(Font.font("System", 14));
            VBox content = new VBox(placeholder);
            content.setAlignment(Pos.CENTER);
            content.getStyleClass().add("tab-body");
            emptyTab.setContent(content);
            tabPane.getTabs().add(emptyTab);
        } else {
            for (TabConfig tabConfig : tabs) {
                Tab tab = buildScriptTab(tabConfig);
                tabPane.getTabs().add(tab);
            }
        }

        return tabPane;
    }

    private Tab buildScriptTab(TabConfig tabConfig) {
        Tab tab = new Tab(tabConfig.getLabel());

        // Main layout: left = buttons, right/bottom = output
        VBox body = new VBox(10);
        body.setPadding(new Insets(14));
        body.getStyleClass().add("tab-body");

        // Script runner for this tab
        ScriptRunner runner = new ScriptRunner();
        tabRunners.put(tabConfig.getIndex(), runner);
        indicatorTabs.put(tabConfig.getIndex(), tab);

        // Output area
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Monospaced", 13));
        outputArea.getStyleClass().add("terminal-area");
        outputArea.setPrefRowCount(25);
        outputArea.setText("Ready. Select a script to run.\n");
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        // Running status label
        Label statusLabel = new Label("● Idle");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusLabel.getStyleClass().add("status-label");

        // Input field and send button (declared here so stopBtn can reference them)
        TextField inputField = new TextField();
        inputField.setPromptText("Type input here and press Enter to send to script...");
        inputField.setDisable(true);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.getStyleClass().add("terminal-input");

        Button sendBtn = new Button("Send");
        sendBtn.setDisable(true);
        styleSmallButton(sendBtn);
        sendBtn.getStyleClass().remove("btn-outline-light");
        sendBtn.getStyleClass().add("btn-secondary");

        // Stop button
        Button stopBtn = new Button("⬛ Stop");
        stopBtn.setDisable(true);
        styleStopButton(stopBtn);
        stopBtn.setOnAction(e -> {
            runner.stop();
            statusLabel.setText("● Idle");
            statusLabel.getStyleClass().remove("status-label-running");
            stopBtn.setDisable(true);
            inputField.setDisable(true);
            sendBtn.setDisable(true);
            inputField.clear();
            setTabRunningStyle(tab, false);
            updateRunningWarning();
        });

        // Clear button
        Button clearBtn = new Button("🗑 Clear");
        styleSmallButton(clearBtn);
        clearBtn.getStyleClass().remove("btn-outline-light");
        clearBtn.getStyleClass().add("btn-outline-secondary");
        clearBtn.setOnAction(e -> outputArea.clear());

        // Script buttons row
        FlowPane buttonBar = new FlowPane(10, 8);
        buttonBar.setPadding(new Insets(4, 0, 4, 0));

        for (ScriptEntry script : tabConfig.getScripts()) {
            Button scriptBtn = new Button("▶ " + script.label());
            styleScriptButton(scriptBtn);

            scriptBtn.setOnAction(e -> {
                if (runner.isRunning()) {
                    outputArea.appendText("⚠ A script is already running. Stop it first.\n");
                    return;
                }
                statusLabel.setText("● Running: " + script.label());
                if (!statusLabel.getStyleClass().contains("status-label-running")) {
                    statusLabel.getStyleClass().add("status-label-running");
                }
                stopBtn.setDisable(false);
                inputField.setDisable(false);
                sendBtn.setDisable(false);
                setTabRunningStyle(tab, true);
                updateRunningWarning();

                runner.run(
                    script,
                    line -> outputArea.appendText(line),
                    () -> {
                        statusLabel.setText("● Idle");
                        statusLabel.getStyleClass().remove("status-label-running");
                        stopBtn.setDisable(true);
                        inputField.setDisable(true);
                        sendBtn.setDisable(true);
                        inputField.clear();
                        setTabRunningStyle(tab, false);
                        updateRunningWarning();
                    }
                );
            });

            // Tooltip with path
            Tooltip tip = new Tooltip(script.scriptPath());
            tip.setStyle("-fx-font-size: 11;");
            Tooltip.install(scriptBtn, tip);

            buttonBar.getChildren().add(scriptBtn);
        }

        // Input bar layout
        HBox inputBar = new HBox(8);
        inputBar.setAlignment(Pos.CENTER_LEFT);
        Label inputLabel = new Label("Input:");
        inputLabel.setTextFill(Color.web(TEXT_SECONDARY));
        inputLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        inputBar.getChildren().addAll(inputLabel, inputField, sendBtn);

        Runnable sendInputTask = () -> {
            String text = inputField.getText();
            if (text != null && !text.isEmpty()) {
                runner.sendInput(text);
                outputArea.appendText("> " + text + "\n");
                inputField.clear();
            }
        };
        inputField.setOnAction(e -> sendInputTask.run());
        sendBtn.setOnAction(e -> sendInputTask.run());

        // Top control bar
        HBox controlBar = new HBox(12);
        controlBar.setAlignment(Pos.CENTER_LEFT);
        controlBar.getChildren().addAll(statusLabel, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, stopBtn, clearBtn);

        body.getChildren().addAll(buttonBar, controlBar, outputArea, inputBar);
        tab.setContent(body);
        return tab;
    }

    // ====================== RUNNING WARNING ======================

    /**
     * Checks all tab runners and shows/hides the header warning label.
     */
    private void updateRunningWarning() {
        boolean anyRunning = tabRunners.values().stream().anyMatch(ScriptRunner::isRunning);
        runningWarningLabel.setVisible(anyRunning);
        runningWarningLabel.setManaged(anyRunning);
    }

    // ====================== FOOTER ======================

    private HBox buildFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(6, 16, 6, 16));
        footer.getStyleClass().add("footer-bar");

        Label info = new Label("AlNao Control Room v1.0 │ Refresh: " + configService.getRefreshSeconds() + "s");
        info.setTextFill(Color.web(TEXT_SECONDARY));
        info.setFont(Font.font("System", 11));
        footer.getChildren().add(info);
        return footer;
    }

    // ====================== STATUS CHECKER ======================

    private void startStatusChecker() {
        if (configService.getMonitors().isEmpty()) return;

        statusChecker = new StatusChecker(
            configService.getMonitors(),
            configService.getRefreshSeconds(),
            results -> Platform.runLater(() -> {
                for (var entry : results.entrySet()) {
                    HBox box = indicatorBoxes.get(entry.getKey().index());
                    Label label = indicatorLabels.get(entry.getKey().index());
                    if (box != null) {
                        boolean alive = entry.getValue();
                        box.getProperties().put("isAlive", alive);
                        applyIndicatorStyle(box, alive ? TEXT_SUCCESS : null);
                        if (label != null) {
                            label.setTextFill(Color.web(TEXT_SECONDARY));
                        }
                    }
                }
            })
        );
        statusChecker.start();
    }

    // ====================== STYLING ======================

    /**
     * Applies a colored background to an indicator box.
     * Pass null to reset to transparent (service not reachable).
     */
    private void applyIndicatorStyle(HBox box, String colorHex) {
        box.getStyleClass().removeAll("indicator-up", "indicator-down", "indicator-checking");
        if (colorHex == null) {
            box.getStyleClass().add("indicator-down");
        } else if (YELLOW_WAIT.equals(colorHex)) {
            box.getStyleClass().add("indicator-checking");
        } else {
            box.getStyleClass().add("indicator-up");
        }
    }

    /**
     * Highlights a tab header green when running, resets to default when idle.
     */
    private void setTabRunningStyle(Tab tab, boolean running) {
        if (running) {
            if (!tab.getStyleClass().contains("running-tab")) {
                tab.getStyleClass().add("running-tab");
            }
        } else {
            tab.getStyleClass().remove("running-tab");
        }
    }

    private void styleScriptButton(Button btn) {
        btn.getStyleClass().addAll("btn", "btn-primary", "script-btn");
    }

    private void styleStopButton(Button btn) {
        btn.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
    }

    private void styleSmallButton(Button btn) {
        btn.getStyleClass().addAll("btn", "btn-outline-light", "btn-sm");
    }

    // ====================== SHUTDOWN ======================

    private void shutdown() {
        if (statusChecker != null) {
            statusChecker.stop();
        }
        for (ScriptRunner runner : tabRunners.values()) {
            runner.stop();
        }
    }
}
