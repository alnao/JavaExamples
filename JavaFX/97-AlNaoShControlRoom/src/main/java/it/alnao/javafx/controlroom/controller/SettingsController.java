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
import java.util.Objects;

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
        stage.setMinHeight(760);
        stage.setMinWidth(980);
        stage.setTitle("Settings - Configure UI");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.getStyleClass().addAll("control-room-root", "settings-root");

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane-bs");

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
        saveBtn.getStyleClass().addAll("btn", "btn-primary");
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
        cancelBtn.getStyleClass().addAll("btn", "btn-outline-secondary");
        cancelBtn.setOnAction(e -> stage.close());

        HBox bottomBox = new HBox(10, saveBtn, cancelBtn);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1220, 820);
        scene.getStylesheets().add(Objects.requireNonNull(
            getClass().getResource("/it/alnao/javafx/controlroom/control-room-bootstrap.css")
        ).toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    private SplitPane buildMonitorsPane() {
        SplitPane split = new SplitPane();

        BorderPane left = new BorderPane();
        left.setPadding(new Insets(10));
        left.getStyleClass().add("panel-card");

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
        left.setCenter(listView);

        BorderPane right = new BorderPane();
        right.setPadding(new Insets(10));
        right.getStyleClass().add("panel-card");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField indexField = new TextField();
        TextField labelField = new TextField();
        TextField urlField = new TextField();

        Label indexLabel = new Label("Index:");
        indexLabel.getStyleClass().add("form-label");
        Label textLabel = new Label("Label:");
        textLabel.getStyleClass().add("form-label");
        Label urlLabel = new Label("URL:");
        urlLabel.getStyleClass().add("form-label");

        form.addRow(0, indexLabel, indexField);
        form.addRow(1, textLabel, labelField);
        form.addRow(2, urlLabel, urlField);

        Button addBtn = new Button("Add / Update");
        addBtn.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().addAll("btn", "btn-danger", "btn-sm");

        HBox buttons = new HBox(10, addBtn, removeBtn);
        form.add(buttons, 1, 3);

        right.setTop(form);

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

        split.getItems().addAll(left, right);
        split.setDividerPositions(0.52);
        return split;
    }

    private SplitPane buildTabsPane() {
        SplitPane split = new SplitPane();

        // TABS
        BorderPane left = new BorderPane();
        left.setPadding(new Insets(10));
        left.getStyleClass().add("panel-card");
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
        Label tabIndexLabel = new Label("Tab Index:");
        tabIndexLabel.getStyleClass().add("form-label");
        Label tabTextLabel = new Label("Tab Label:");
        tabTextLabel.getStyleClass().add("form-label");
        tabForm.addRow(0, tabIndexLabel, tabIndexField);
        tabForm.addRow(1, tabTextLabel, tabLabelField);
        Button addTabBtn = new Button("Add / Update Tab");
        addTabBtn.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button remTabBtn = new Button("Remove Tab");
        remTabBtn.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
        tabForm.add(new HBox(10, addTabBtn, remTabBtn), 1, 2);

        left.setCenter(tabList);
        left.setBottom(tabForm);

        // SCRIPTS
        BorderPane right = new BorderPane();
        right.setPadding(new Insets(10));
        right.getStyleClass().add("panel-card");
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
        Label scriptIndexLabel = new Label("Script Idx:");
        scriptIndexLabel.getStyleClass().add("form-label");
        Label scriptTextLabel = new Label("Script Lbl:");
        scriptTextLabel.getStyleClass().add("form-label");
        Label scriptPathLabel = new Label("Path:");
        scriptPathLabel.getStyleClass().add("form-label");
        Label scriptParamsLabel = new Label("Params:");
        scriptParamsLabel.getStyleClass().add("form-label");

        scriptForm.addRow(0, scriptIndexLabel, scrIndexField);
        scriptForm.addRow(1, scriptTextLabel, scrLabelField);
        scriptForm.addRow(2, scriptPathLabel, scrPathField);
        scriptForm.addRow(3, scriptParamsLabel, scrParamsField);
        Button addScrBtn = new Button("Add/Upd Script");
        addScrBtn.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button remScrBtn = new Button("Remove Script");
        remScrBtn.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
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
        box.getStyleClass().add("panel-card");
        HBox row = new HBox(10);
        Label refreshLabel = new Label("Monitor Refresh Seconds:");
        refreshLabel.getStyleClass().add("form-label");
        row.getChildren().add(refreshLabel);
        refreshField = new TextField(String.valueOf(configService.getRefreshSeconds()));
        row.getChildren().add(refreshField);
        box.getChildren().add(row);
        return box;
    }
}
