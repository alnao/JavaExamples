# Progetto 04 - Employee Management Table

## 📊 Descrizione
Applicazione JavaFX per gestire una tabella di dipendenti con operazioni CRUD complete (Create, Read, Update, Delete).

## 🎯 Concetto Principale: TableView e TableColumn

### Cos'è una TableView?
Una **TableView** è un controllo JavaFX per visualizzare dati tabulari in righe e colonne, simile a una tabella Excel o a un DataGrid.

### Architettura TableView
```
TableView<Employee>
  ├── TableColumn<Employee, Integer> (ID)
  ├── TableColumn<Employee, String> (Nome)
  ├── TableColumn<Employee, String> (Ruolo)
  └── TableColumn<Employee, Double> (Stipendio)
```

### PropertyValueFactory
Collega automaticamente le colonne alle proprietà del POJO:
```java
TableColumn<Employee, String> nameCol = new TableColumn<>("Nome");
nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
// Cerca il metodo nameProperty() nella classe Employee
```

### POJO con JavaFX Properties
```java
public class Employee {
    private final StringProperty name = new SimpleStringProperty();
    
    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
}
```

## 🔄 Operazioni CRUD Implementate

### 1. CREATE (Aggiungi)
```java
Employee newEmp = new Employee(nextId++, name, role, salary);
employeeList.add(newEmp); // TableView si aggiorna automaticamente!
```

### 2. READ (Visualizza)
```java
TableView<Employee> table = new TableView<>(employeeList);
// La tabella mostra automaticamente tutti i dipendenti
```

### 3. UPDATE (Modifica)
```java
Employee selected = table.getSelectionModel().getSelectedItem();
selected.setName("Nuovo nome");
selected.setSalary(50000);
table.refresh(); // Aggiorna la visualizzazione
```

### 4. DELETE (Elimina)
```java
Employee selected = table.getSelectionModel().getSelectedItem();
employeeList.remove(selected); // TableView si aggiorna automaticamente!
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Concetto | Tipo di Dato | Visualizzazione |
|----------|----------|--------------|-----------------|
| 01 - Click Counter | Event Handling | Singolo intero | Label |
| 02 - Unit Converter | Property Binding | Singolo double | TextField |
| 03 - To-Do List | ObservableList | Lista di stringhe | ListView |
| **04 - Employee Table** | **TableView** | **Lista di oggetti** | **Tabella multi-colonna** |

### Evoluzione della Gestione Dati
- **Progetto 03**: Lista di stringhe semplici (`ObservableList<String>`)
- **Progetto 04**: Lista di oggetti complessi (`ObservableList<Employee>`) con proprietà multiple

## 🏗️ Componenti JavaFX Utilizzati

### TableView e TableColumn
```java
TableView<Employee> table = new TableView<>();
TableColumn<Employee, String> col = new TableColumn<>("Colonna");
col.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
table.getColumns().add(col);
```

### Custom CellFactory
Personalizza la visualizzazione delle celle:
```java
salaryCol.setCellFactory(tc -> new TableCell<Employee, Double>() {
    @Override
    protected void updateItem(Double value, boolean empty) {
        super.updateItem(value, empty);
        setText(empty ? null : String.format("%.2f €", value));
    }
});
```

### SelectionModel
Gestisce la selezione delle righe:
```java
table.getSelectionModel().selectedItemProperty().addListener(
    (obs, oldVal, newVal) -> fillFormWithEmployee(newVal)
);
```

### GridPane
Layout a griglia per i form:
```java
GridPane grid = new GridPane();
grid.add(label, 0, 0);   // colonna 0, riga 0
grid.add(textField, 1, 0); // colonna 1, riga 0
```

## 🎨 Funzionalità Implementate

1. **Visualizzazione Tabella**: TableView con 4 colonne (ID, Nome, Ruolo, Stipendio)
2. **Aggiungi Dipendente**: Form + pulsante "Aggiungi"
3. **Modifica Dipendente**: Seleziona riga → Modifica dati → "Modifica"
4. **Elimina Dipendente**: Seleziona riga → Conferma eliminazione
5. **Auto-Fill Form**: Cliccando su una riga, il form si popola automaticamente
6. **Validazione Input**: Controllo campi obbligatori e formato numerico
7. **Formattazione Custom**: Stipendi con 2 decimali e simbolo €

## 📦 Compilazione ed Esecuzione

```bash
# Compila il progetto
mvn clean compile

# Esegui l'applicazione
mvn javafx:run
```

## 🎓 Concetti Appresi

1. **TableView**: Controllo per dati tabulari
2. **TableColumn**: Definizione delle colonne
3. **PropertyValueFactory**: Binding automatico colonne → proprietà POJO
4. **POJO con Property**: StringProperty, IntegerProperty, DoubleProperty
5. **CellFactory**: Personalizzazione celle (formattazione)
6. **SelectionModel**: Gestione selezione righe
7. **GridPane**: Layout a griglia per form
8. **CRUD Operations**: Create, Read, Update, Delete
9. **Validazione Form**: Controlli sui dati inseriti
10. **Auto-Fill Pattern**: Popolare form da selezione tabella



# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.