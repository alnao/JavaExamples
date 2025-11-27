package it.alnao.javafx.employeetable;

import javafx.beans.property.*;

/**
 * Classe POJO per rappresentare un dipendente
 * Utilizza JavaFX Property per il binding automatico con TableView
 */
public class Employee {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty role;
    private final DoubleProperty salary;

    public Employee(int id, String name, String role, double salary) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.salary = new SimpleDoubleProperty(salary);
    }

    // Getter per le Property (usate da TableColumn)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty roleProperty() { return role; }
    public DoubleProperty salaryProperty() { return salary; }

    // Getter normali
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getRole() { return role.get(); }
    public double getSalary() { return salary.get(); }

    // Setter
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setRole(String role) { this.role.set(role); }
    public void setSalary(double salary) { this.salary.set(salary); }
}
