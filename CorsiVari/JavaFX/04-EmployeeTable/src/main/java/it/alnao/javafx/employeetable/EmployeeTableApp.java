package it.alnao.javafx.employeetable;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Progetto 04 - Employee Management Table
 * Concetto principale: TableView e TableColumn
 * 
 * Questo progetto introduce TableView per visualizzare dati tabulari
 * con operazioni CRUD (Create, Read, Update, Delete) complete.
 */
public class EmployeeTableApp extends Application {
    
    private ObservableList<Employee> employeeList;
    private TableView<Employee> tableView;
    private TextField idField, nameField, roleField, salaryField;
    private int nextId = 4;

    @Override
    public void start(Stage primaryStage) {
        // Inizializza la lista con dati di esempio
        employeeList = FXCollections.observableArrayList(
            new Employee(1, "Mario Rossi", "Manager", 45000),
            new Employee(2, "Laura Bianchi", "Developer", 35000),
            new Employee(3, "Giovanni Verdi", "Designer", 32000)
        );

        // Titolo
        Label titleLabel = new Label("👥 Gestione Dipendenti");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        // TableView
        tableView = createTableView();

        // Form di input
        GridPane formGrid = createInputForm();

        // Pulsanti CRUD
        HBox buttonBox = createCrudButtons();

        // Layout principale
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setMaxWidth(800);
        card.getChildren().addAll(titleLabel, tableView, formGrid, buttonBox);

        root.getChildren().add(card);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Employee Management - TableView Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crea la TableView con le colonne
     */
    @SuppressWarnings("unchecked")
    private TableView<Employee> createTableView() {
        TableView<Employee> table = new TableView<>(employeeList);
        
        // Colonna ID
        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        // Colonna Nome
        TableColumn<Employee, String> nameCol = new TableColumn<>("Nome");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        // Colonna Ruolo
        TableColumn<Employee, String> roleCol = new TableColumn<>("Ruolo");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(180);

        // Colonna Stipendio
        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Stipendio (€)");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryCol.setPrefWidth(150);
        
        // Formatta gli stipendi con 2 decimali
        salaryCol.setCellFactory(tc -> new TableCell<Employee, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", value));
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, roleCol, salaryCol);
        table.setPrefHeight(300);
        table.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5px;"
        );

        // Listener per riempire il form quando si seleziona una riga
        table.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFormWithEmployee(newSelection);
                }
            }
        );

        return table;
    }

    /**
     * Crea il form di input
     */
    private GridPane createInputForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Labels
        Label nameLabel = new Label("Nome:");
        Label roleLabel = new Label("Ruolo:");
        Label salaryLabel = new Label("Stipendio:");

        // TextFields
        idField = new TextField();
        idField.setVisible(false);
        
        nameField = new TextField();
        nameField.setPromptText("Nome completo");
        
        roleField = new TextField();
        roleField.setPromptText("Ruolo");
        
        salaryField = new TextField();
        salaryField.setPromptText("Es: 35000");

        // Stile campi
        String fieldStyle = 
            "-fx-background-color: white; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-padding: 5px 10px;";
        
        nameField.setStyle(fieldStyle);
        roleField.setStyle(fieldStyle);
        salaryField.setStyle(fieldStyle);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(roleLabel, 2, 0);
        grid.add(roleField, 3, 0);
        grid.add(salaryLabel, 4, 0);
        grid.add(salaryField, 5, 0);

        return grid;
    }

    /**
     * Crea i pulsanti CRUD
     */
    private HBox createCrudButtons() {
        Button addButton = createStyledButton("➕ Aggiungi", "#198754", "#146c43");
        addButton.setOnAction(e -> addEmployee());

        Button updateButton = createStyledButton("✏️ Modifica", "#0d6efd", "#0b5ed7");
        updateButton.setOnAction(e -> updateEmployee());

        Button deleteButton = createStyledButton("🗑️ Elimina", "#dc3545", "#bb2d3b");
        deleteButton.setOnAction(e -> deleteEmployee());

        Button clearButton = createStyledButton("🔄 Pulisci", "#6c757d", "#5c636a");
        clearButton.setOnAction(e -> clearForm());

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        
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
     * Aggiunge un nuovo dipendente
     */
    private void addEmployee() {
        if (validateForm()) {
            Employee newEmp = new Employee(
                nextId++,
                nameField.getText().trim(),
                roleField.getText().trim(),
                Double.parseDouble(salaryField.getText().trim())
            );
            employeeList.add(newEmp);
            clearForm();
            showInfo("Dipendente aggiunto con successo!");
        }
    }

    /**
     * Modifica il dipendente selezionato
     */
    private void updateEmployee() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Seleziona un dipendente da modificare!");
            return;
        }
        
        if (validateForm()) {
            selected.setName(nameField.getText().trim());
            selected.setRole(roleField.getText().trim());
            selected.setSalary(Double.parseDouble(salaryField.getText().trim()));
            tableView.refresh();
            clearForm();
            showInfo("Dipendente modificato con successo!");
        }
    }

    /**
     * Elimina il dipendente selezionato
     */
    private void deleteEmployee() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Seleziona un dipendente da eliminare!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText("Eliminare " + selected.getName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                employeeList.remove(selected);
                clearForm();
                showInfo("Dipendente eliminato con successo!");
            }
        });
    }

    /**
     * Riempie il form con i dati del dipendente selezionato
     */
    private void fillFormWithEmployee(Employee emp) {
        idField.setText(String.valueOf(emp.getId()));
        nameField.setText(emp.getName());
        roleField.setText(emp.getRole());
        salaryField.setText(String.valueOf(emp.getSalary()));
    }

    /**
     * Pulisce il form
     */
    private void clearForm() {
        idField.clear();
        nameField.clear();
        roleField.clear();
        salaryField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    /**
     * Valida i campi del form
     */
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Il nome è obbligatorio!");
            return false;
        }
        if (roleField.getText().trim().isEmpty()) {
            showAlert("Il ruolo è obbligatorio!");
            return false;
        }
        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            if (salary < 0) {
                showAlert("Lo stipendio deve essere positivo!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Inserisci uno stipendio valido!");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attenzione");
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

    public static void main(String[] args) {
        launch(args);
    }
}
