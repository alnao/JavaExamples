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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

/**
 * Main controller that builds the Control Room UI:
 * - Header bar with status indicators (red/green circles)
 * - TabPane where each tab has script buttons + output textarea + stop button
 */
public class ControlRoomController {

    // -- Color palette --
    private static final String BG_DARK       = "#1a1a2e";
    private static final String BG_HEADER     = "#16213e";
    private static final String BG_TAB_PANE   = "#0f3460";
    private static final String BG_TAB_BODY   = "#1a1a2e";
    private static final String ACCENT        = "#ff6b35";
    private static final String ACCENT_HOVER  = "#ff6b81";
    private static final String BTN_BG        = "#0f3460";
    private static final String BTN_HOVER     = "#1a5276";
    private static final String TEXT_PRIMARY   = "#eaeaea";
    private static final String TEXT_SECONDARY = "#a0a0b0";
    private static final String GREEN_ON      = "#268c22";
    private static final String RED_OFF       = "#f44336";
    private static final String YELLOW_WAIT   = "#f39c12";
    private static final String TEXTAREA_BG   = "#0d1117";

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
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

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
        scene.getStylesheets().add(createInlineCSS());

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
        header.setStyle(
            "-fx-background-color: " + BG_HEADER + ";" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-width: 0 0 2 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(233,69,96,0.3), 12, 0, 0, 3);"
        );

        // Title
        Label title = new Label("⚡");// Control Room
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web(ACCENT));
        title.setPadding(new Insets(0, 8, 0, 0));

        // Running warning label (hidden by default)
        runningWarningLabel = new Label("⚠");// Something is running
        runningWarningLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        runningWarningLabel.setTextFill(Color.web(YELLOW_WAIT));
        runningWarningLabel.setStyle(
            "-fx-background-color: rgba(243,156,18,0.15);" +
            "-fx-padding: 3 10;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + YELLOW_WAIT + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        runningWarningLabel.setVisible(false);
        runningWarningLabel.setManaged(false);

        Separator sep = new Separator();
        sep.setOrientation(javafx.geometry.Orientation.VERTICAL);
        sep.setStyle("-fx-background-color: " + TEXT_SECONDARY + ";");
        sep.setPrefHeight(30);

        header.getChildren().addAll(title, runningWarningLabel, sep);

        // Indicators
        for (MonitorEntry monitor : configService.getMonitors()) {
            HBox indicator = buildIndicator(monitor);
            header.getChildren().add(indicator);
        }

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        // Refresh button
        Button refreshBtn = new Button("🔄 Refresh");
        styleSmallButton(refreshBtn);
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
        box.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(255,255,255,0.1);" +
            "-fx-border-radius: 20;"
        );


        Label label = new Label(monitor.label());
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        label.setTextFill(Color.web(TEXT_PRIMARY));

        // Tooltip with URL
        Tooltip tip = new Tooltip(monitor.url());
        tip.setStyle("-fx-font-size: 11;");
        Tooltip.install(box, tip);

        // Store the box (not a circle) for background updates
        indicatorBoxes.put(monitor.index(), box);
        indicatorLabels.put(monitor.index(), label);

        // Initial state: yellow (checking)
        applyIndicatorStyle(box, YELLOW_WAIT);

        box.getChildren().add(label);

        // --- Play button: open URL in browser ---
        Button playBtn = new Button("▶");
        styleIconButton(playBtn, "#FFFFFF");
        Tooltip.install(playBtn, new Tooltip("Open in browser: " + monitor.url()));
        playBtn.setOnAction(e -> openInBrowser(monitor.url()));
        box.getChildren().add(playBtn);

        // --- Stop button: kill process on port (localhost only) ---
        boolean isLocalhost = monitor.url().contains("localhost") || monitor.url().contains("127.0.0.1");
        if (isLocalhost) {
            Button stopBtn = new Button("⬛");
            styleIconButton(stopBtn, RED_OFF);
            int port = extractPort(monitor.url());
            Tooltip.install(stopBtn, new Tooltip("Kill process on port " + port));
            stopBtn.setOnAction(e -> {
                if (port > 0) {
                    killProcessOnPort(port);
                    // Trigger a status re-check after a short delay
                    new Thread(() -> {
                        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                        if (statusChecker != null) statusChecker.checkAll();
                    }, "post-kill-refresh").start();
                }
            });
            box.getChildren().add(stopBtn);
        }

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
     * Extracts port number from a URL string like "http://localhost:8042/path".
     */
    private int extractPort(String urlStr) {
        try {
            String full = urlStr;
            if (!full.startsWith("http://") && !full.startsWith("https://")) {
                full = "http://" + full;
            }
            URI uri = URI.create(full);
            int port = uri.getPort();
            if (port > 0) return port;
            // default ports
            if (full.startsWith("https")) return 443;
            return 80;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Kills the process listening on the given port using `fuser -k`.
     */
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

    // ====================== TAB PANE ======================

    private TabPane buildTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(
            "-fx-background-color: " + BG_TAB_PANE + ";"
        );
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
            content.setStyle("-fx-background-color: " + BG_TAB_BODY + ";");
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
        body.setStyle("-fx-background-color: " + BG_TAB_BODY + ";");

        // Script runner for this tab
        ScriptRunner runner = new ScriptRunner();
        tabRunners.put(tabConfig.getIndex(), runner);
        indicatorTabs.put(tabConfig.getIndex(), tab);

        // Output area
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Monospaced", 13));
        outputArea.setStyle(
            "-fx-control-inner-background: " + TEXTAREA_BG + ";" +
            "-fx-text-fill: " + GREEN_ON + ";" +
            "-fx-font-family: 'Monospaced';" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
        outputArea.setPrefRowCount(25);
        outputArea.setText("Ready. Select a script to run.\n");
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        // Running status label
        Label statusLabel = new Label("● Idle");
        statusLabel.setTextFill(Color.web(TEXT_SECONDARY));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        // Input field and send button (declared here so stopBtn can reference them)
        TextField inputField = new TextField();
        inputField.setPromptText("Type input here and press Enter to send to script...");
        inputField.setDisable(true);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setStyle(
            "-fx-background-color: " + TEXTAREA_BG + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-family: 'Monospaced';" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;"
        );

        Button sendBtn = new Button("Send");
        sendBtn.setDisable(true);
        styleSmallButton(sendBtn);

        // Stop button
        Button stopBtn = new Button("⬛ Stop");
        stopBtn.setDisable(true);
        styleStopButton(stopBtn);
        stopBtn.setOnAction(e -> {
            runner.stop();
            statusLabel.setText("● Idle");
            statusLabel.setTextFill(Color.web(TEXT_SECONDARY));
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
                statusLabel.setTextFill(Color.web(GREEN_ON));
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
                        statusLabel.setTextFill(Color.web(TEXT_SECONDARY));
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
        footer.setStyle(
            "-fx-background-color: " + BG_HEADER + ";" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-width: 1 0 0 0;"
        );

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
                        applyIndicatorStyle(box, alive ? GREEN_ON : null);
                        if (label != null) {
                            label.setTextFill(Color.web(alive ? TEXT_PRIMARY : TEXT_SECONDARY));
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
        if (colorHex == null) {
            // Not reachable: transparent, dim border
            box.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: rgba(255,255,255,0.12);" +
                "-fx-border-radius: 20;" +
                "-fx-border-width: 1;"
            );
        } else {
            // Reachable or checking: solid color + glow
            box.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + colorHex + ";" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, " + colorHex + ", 8, 0.3, 0, 0);"
            );
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
        btn.setStyle(
            "-fx-background-color: " + BTN_BG + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + BTN_HOVER + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + ACCENT_HOVER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(233,69,96,0.4), 8, 0, 0, 2);"
        ));
        btn.setOnMouseExited(e -> styleScriptButton(btn));
    }

    private void styleStopButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: " + RED_OFF + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 16;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
    }

    private void styleIconButton(Button btn, String color) {
        btn.setMinSize(22, 22);
        btn.setMaxSize(22, 22);
        btn.setPadding(Insets.EMPTY);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-font-size: 11;" +
            "-fx-padding: 0;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 12;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11;" +
            "-fx-padding: 0;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 12;"
        ));
        btn.setOnMouseExited(e -> styleIconButton(btn, color));
    }

    private void styleSmallButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 12;" +
            "-fx-padding: 6 12;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + TEXT_SECONDARY + ";" +
            "-fx-border-radius: 6;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12;" +
            "-fx-padding: 6 12;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: white;" +
            "-fx-border-radius: 6;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> styleSmallButton(btn));
    }

    /**
     * Inline CSS stylesheet as a data URI for the tab pane styling.
     */
    private String createInlineCSS() {//quiquiqui
        String css = """
            .tab-pane > .tab-header-area {
                -fx-padding: 4 8 0 8;
            }
            .tab-pane > .tab-header-area > .tab-header-background {
                -fx-background-color: #16213e;
            }
            .tab {
                -fx-background-color: #0f3460;
                -fx-background-radius: 8 8 0 0;
                -fx-padding: 6 18;
            }
            .tab:selected {
                -fx-background-color: #234d80;
            }
            .tab.running-tab {
                -fx-background-color: #2ecc71;
            }
            .tab.running-tab:selected {
                -fx-background-color: #2ecc71;
            }
            .tab .tab-label {
                -fx-text-fill: #a0a0b0;
                -fx-font-weight: bold;
                -fx-font-size: 13;
            }
            .tab:selected .tab-label {
                -fx-text-fill: white;
            }
            .tab.running-tab .tab-label, .tab.running-tab:selected .tab-label {
                -fx-text-fill: #16213e;
            }
            .scroll-bar {
                -fx-background-color: #1a1a2e;
            }
            .scroll-bar .thumb {
                -fx-background-color: #234d80;
                -fx-background-radius: 4;
            }
            .scroll-bar .increment-button, .scroll-bar .decrement-button {
                -fx-background-color: transparent;
                -fx-padding: 0;
            }
            .scroll-bar .increment-arrow, .scroll-bar .decrement-arrow {
                -fx-shape: " ";
                -fx-padding: 0;
            }
            .text-area .content {
                -fx-background-color: #0d1117;
            }
            .text-area {
                -fx-background-color: #0d1117;
            }
            """;

        // Write CSS to a temp file and return its URL
        try {
            java.nio.file.Path tmpCss = java.nio.file.Files.createTempFile("controlroom-", ".css");
            tmpCss.toFile().deleteOnExit();
            java.nio.file.Files.writeString(tmpCss, css);
            return tmpCss.toUri().toString();
        } catch (Exception e) {
            System.err.println("[ControlRoom] Failed to create CSS file: " + e.getMessage());
            return "";
        }
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
