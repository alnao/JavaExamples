package it.alnao.javafx.controlroom.controller;

import it.alnao.javafx.controlroom.model.MonitorEntry;
import it.alnao.javafx.controlroom.model.ScriptEntry;
import it.alnao.javafx.controlroom.model.TabConfig;
import it.alnao.javafx.controlroom.service.ConfigService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SettingsController {

    private final ConfigService configService;

    private final ObservableList<MonitorEntry> monitors = FXCollections.observableArrayList();
    private final ObservableList<TabConfig> tabs = FXCollections.observableArrayList();
    private TextField refreshField;

    public SettingsController(ConfigService configService) {
        this.configService = configService;
        this.monitors.addAll(configService.getMonitors());

        for (TabConfig t : configService.getTabs()) {
            TabConfig cloned = new TabConfig(t.getIndex(), t.getLabel());
            cloned.setScripts(new ArrayList<>(t.getScripts()));
            this.tabs.add(cloned);
        }
    }

    public void showSettingsWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(600);
        stage.setMinWidth(600);
        stage.setTitle("Settings - Configure UI");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();

        Tab monitorsTab = new Tab("Monitors");
        monitorsTab.setClosable(false);
        monitorsTab.setContent(buildMonitorsPane());

        Tab tabsTab = new Tab("Tabs & Scripts");
        tabsTab.setClosable(false);
        tabsTab.setContent(buildTabsPane());

        Tab generalTab = new Tab("General");
        generalTab.setClosable(false);
        generalTab.setContent(buildGeneralPane());

        tabPane.getTabs().addAll(monitorsTab, tabsTab, generalTab);
        root.setCenter(tabPane);

        Button saveBtn = new Button("Save & Close");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            try {
                configService.setRefreshSeconds(Integer.parseInt(refreshField.getText()));
            } catch (Exception ignored) {
            }

            configService.setMonitors(new ArrayList<>(monitors));
            configService.setTabs(new ArrayList<>(tabs));
            configService.save();

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Configuration saved successfully. Please restart the application to apply changes.",
                    ButtonType.OK);
            alert.showAndWait();
            stage.close();
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> stage.close());

        HBox bottomBox = new HBox(10, saveBtn, cancelBtn);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 900, 600);
        scene.getRoot().setStyle("-fx-font-family: 'System'; -fx-font-size: 13px;");
        stage.setScene(scene);
        stage.showAndWait();
    }

    private BorderPane buildMonitorsPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        ListView<MonitorEntry> listView = new ListView<>(monitors);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MonitorEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.index() + " - " + item.label() + " (" + item.url() + ")");
                }
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(0, 0, 0, 10));

        TextField indexField = new TextField();
        TextField labelField = new TextField();
        TextField urlField = new TextField();

        form.addRow(0, new Label("Index:"), indexField);
        form.addRow(1, new Label("Label:"), labelField);
        form.addRow(2, new Label("URL:"), urlField);

        Button addBtn = new Button("Add / Update");
        Button removeBtn = new Button("Remove");

        HBox buttons = new HBox(10, addBtn, removeBtn);
        form.add(buttons, 1, 3);

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                indexField.setText(String.valueOf(newV.index()));
                labelField.setText(newV.label());
                urlField.setText(newV.url());
            }
        });

        addBtn.setOnAction(e -> {
            try {
                int idx = Integer.parseInt(indexField.getText());
                MonitorEntry m = new MonitorEntry(idx, labelField.getText(), urlField.getText());

                int existingIdx = -1;
                for (int i = 0; i < monitors.size(); i++) {
                    if (monitors.get(i).index() == idx)
                        existingIdx = i;
                }
                if (existingIdx >= 0) {
                    monitors.set(existingIdx, m);
                } else {
                    monitors.add(m);
                }
                listView.getSelectionModel().select(m);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Index", ButtonType.OK).show();
            }
        });

        removeBtn.setOnAction(e -> {
            MonitorEntry sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null)
                monitors.remove(sel);
        });

        pane.setLeft(listView);
        pane.setCenter(form);
        return pane;
    }

    private SplitPane buildTabsPane() {
        SplitPane split = new SplitPane();

        // TABS
        BorderPane left = new BorderPane();
        left.setPadding(new Insets(10));
        ListView<TabConfig> tabList = new ListView<>(tabs);
        tabList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TabConfig item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getIndex() + " - " + item.getLabel());
            }
        });

        GridPane tabForm = new GridPane();
        tabForm.setHgap(10);
        tabForm.setVgap(10);
        tabForm.setPadding(new Insets(10, 0, 0, 0));
        TextField tabIndexField = new TextField();
        TextField tabLabelField = new TextField();
        tabForm.addRow(0, new Label("Tab Index:"), tabIndexField);
        tabForm.addRow(1, new Label("Tab Label:"), tabLabelField);
        Button addTabBtn = new Button("Add / Update Tab");
        Button remTabBtn = new Button("Remove Tab");
        tabForm.add(new HBox(10, addTabBtn, remTabBtn), 1, 2);

        left.setCenter(tabList);
        left.setBottom(tabForm);

        // SCRIPTS
        BorderPane right = new BorderPane();
        right.setPadding(new Insets(10));
        ObservableList<ScriptEntry> currentScripts = FXCollections.observableArrayList();
        ListView<ScriptEntry> scriptList = new ListView<>(currentScripts);
        scriptList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ScriptEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.scriptIndex() + " - " + item.label() + " [" + item.scriptPath() + "]");
            }
        });

        GridPane scriptForm = new GridPane();
        scriptForm.setHgap(10);
        scriptForm.setVgap(10);
        scriptForm.setPadding(new Insets(10, 0, 0, 0));
        TextField scrIndexField = new TextField();
        TextField scrLabelField = new TextField();
        TextField scrPathField = new TextField();
        TextField scrParamsField = new TextField();
        scriptForm.addRow(0, new Label("Script Idx:"), scrIndexField);
        scriptForm.addRow(1, new Label("Script Lbl:"), scrLabelField);
        scriptForm.addRow(2, new Label("Path:"), scrPathField);
        scriptForm.addRow(3, new Label("Params:"), scrParamsField);
        Button addScrBtn = new Button("Add/Upd Script");
        Button remScrBtn = new Button("Remove Script");
        scriptForm.add(new HBox(10, addScrBtn, remScrBtn), 1, 4);

        right.setCenter(scriptList);
        right.setBottom(scriptForm);

        // Bindings Tab -> Scripts
        tabList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                tabIndexField.setText(String.valueOf(newV.getIndex()));
                tabLabelField.setText(newV.getLabel());
                currentScripts.setAll(newV.getScripts());
            } else {
                currentScripts.clear();
            }
        });

        addTabBtn.setOnAction(e -> {
            try {
                int idx = Integer.parseInt(tabIndexField.getText());
                TabConfig existing = null;
                for (TabConfig t : tabs) {
                    if (t.getIndex() == idx) {
                        existing = t;
                        break;
                    }
                }
                if (existing != null) {
                    existing.setLabel(tabLabelField.getText());
                    tabList.refresh();
                } else {
                    tabs.add(new TabConfig(idx, tabLabelField.getText()));
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Tab Index").show();
            }
        });

        remTabBtn.setOnAction(e -> {
            TabConfig sel = tabList.getSelectionModel().getSelectedItem();
            if (sel != null)
                tabs.remove(sel);
        });

        // Script bindings
        scriptList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                scrIndexField.setText(String.valueOf(newV.scriptIndex()));
                scrLabelField.setText(newV.label());
                scrPathField.setText(newV.scriptPath());
                scrParamsField.setText(newV.params());
            }
        });

        addScrBtn.setOnAction(e -> {
            TabConfig selTab = tabList.getSelectionModel().getSelectedItem();
            if (selTab == null)
                return;
            try {
                int idx = Integer.parseInt(scrIndexField.getText());
                ScriptEntry se = new ScriptEntry(selTab.getIndex(), idx, scrLabelField.getText(),
                        scrPathField.getText(), scrParamsField.getText());

                int existingIdx = -1;
                for (int i = 0; i < currentScripts.size(); i++) {
                    if (currentScripts.get(i).scriptIndex() == idx)
                        existingIdx = i;
                }
                if (existingIdx >= 0) {
                    currentScripts.set(existingIdx, se);
                } else {
                    currentScripts.add(se);
                }
                selTab.setScripts(new ArrayList<>(currentScripts));
                scriptList.refresh();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Script Index").show();
            }
        });

        remScrBtn.setOnAction(e -> {
            TabConfig selTab = tabList.getSelectionModel().getSelectedItem();
            ScriptEntry selScr = scriptList.getSelectionModel().getSelectedItem();
            if (selTab != null && selScr != null) {
                currentScripts.remove(selScr);
                selTab.setScripts(new ArrayList<>(currentScripts));
            }
        });

        split.getItems().addAll(left, right);
        split.setDividerPositions(0.4);
        return split;
    }

    private VBox buildGeneralPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        HBox row = new HBox(10);
        row.getChildren().add(new Label("Monitor Refresh Seconds:"));
        refreshField = new TextField(String.valueOf(configService.getRefreshSeconds()));
        row.getChildren().add(refreshField);
        box.getChildren().add(row);
        return box;
    }
}
