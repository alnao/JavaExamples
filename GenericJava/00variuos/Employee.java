//Fare una classe di nome «employed» che abbia come unico attributo un’intero e fornire un’implementazione delle classi «manager» ed «employed administrator»
/**
 * Classe base per rappresentare un dipendente.
 * Ha come unico attributo un intero (id).
 */
public class Employee {
    private int id;  // Identificativo univoco del dipendente
    
    /**
     * Costruttore che inizializza l'id del dipendente.
     * 
     * @param id l'identificativo del dipendente
     */
    public Employee(int id) {
        this.id = id;
    }
    
    /**
     * Restituisce l'id del dipendente.
     * 
     * @return l'identificativo del dipendente
     */
    public int getId() {
        return id;
    }
    
    /**
     * Imposta l'id del dipendente.
     * 
     * @param id il nuovo identificativo del dipendente
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Restituisce una rappresentazione testuale del dipendente.
     * 
     * @return una stringa che rappresenta il dipendente
     */
    @Override
    public String toString() {
        return "Employee [id=" + id + "]";
    }
}

/**
 * Classe che rappresenta un manager, estende la classe Employee.
 */
public class Manager extends Employee {
    private String department;   // Dipartimento gestito dal manager
    private int teamSize;        // Dimensione del team gestito
    
    /**
     * Costruttore che inizializza un manager con id, dipartimento e dimensione del team.
     * 
     * @param id l'identificativo del manager
     * @param department il dipartimento gestito
     * @param teamSize la dimensione del team
     */
    public Manager(int id, String department, int teamSize) {
        super(id);  // Inizializza l'id chiamando il costruttore della classe base
        this.department = department;
        this.teamSize = teamSize;
    }
    
    /**
     * Restituisce il dipartimento gestito dal manager.
     * 
     * @return il nome del dipartimento
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Imposta il dipartimento gestito dal manager.
     * 
     * @param department il nuovo dipartimento
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Restituisce la dimensione del team gestito dal manager.
     * 
     * @return il numero di dipendenti nel team
     */
    public int getTeamSize() {
        return teamSize;
    }
    
    /**
     * Imposta la dimensione del team gestito dal manager.
     * 
     * @param teamSize la nuova dimensione del team
     */
    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }
    
    /**
     * Metodo per assegnare un task a un dipendente (simulato).
     * 
     * @param taskName il nome del task da assegnare
     * @param employeeId l'id del dipendente a cui assegnare il task
     * @return true se l'assegnazione è avvenuta con successo
     */
    public boolean assignTask(String taskName, int employeeId) {
        // Implementazione simulata dell'assegnazione di un task
        System.out.println("Manager " + getId() + " ha assegnato il task '" + 
                           taskName + "' al dipendente " + employeeId);
        return true;
    }
    
    /**
     * Restituisce una rappresentazione testuale del manager.
     * 
     * @return una stringa che rappresenta il manager
     */
    @Override
    public String toString() {
        return "Manager [id=" + getId() + ", department=" + department + 
               ", teamSize=" + teamSize + "]";
    }
}

/**
 * Classe che rappresenta un amministratore di dipendenti, estende la classe Employee.
 */
public class EmployeeAdministrator extends Employee {
    private String accessLevel;  // Livello di accesso dell'amministratore
    private String region;       // Regione di competenza
    
    /**
     * Costruttore che inizializza un amministratore con id, livello di accesso e regione.
     * 
     * @param id l'identificativo dell'amministratore
     * @param accessLevel il livello di accesso (es. "BASE", "AVANZATO", "COMPLETO")
     * @param region la regione di competenza
     */
    public EmployeeAdministrator(int id, String accessLevel, String region) {
        super(id);  // Inizializza l'id chiamando il costruttore della classe base
        this.accessLevel = accessLevel;
        this.region = region;
    }
    
    /**
     * Restituisce il livello di accesso dell'amministratore.
     * 
     * @return il livello di accesso
     */
    public String getAccessLevel() {
        return accessLevel;
    }
    
    /**
     * Imposta il livello di accesso dell'amministratore.
     * 
     * @param accessLevel il nuovo livello di accesso
     */
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    /**
     * Restituisce la regione di competenza dell'amministratore.
     * 
     * @return la regione
     */
    public String getRegion() {
        return region;
    }
    
    /**
     * Imposta la regione di competenza dell'amministratore.
     * 
     * @param region la nuova regione
     */
    public void setRegion(String region) {
        this.region = region;
    }
    
    /**
     * Metodo per registrare un nuovo dipendente (simulato).
     * 
     * @param name il nome del dipendente
     * @param position la posizione lavorativa
     * @return l'id assegnato al nuovo dipendente
     */
    public int registerEmployee(String name, String position) {
        // Implementazione simulata della registrazione di un dipendente
        int newId = (int)(Math.random() * 10000);  // Genera un id casuale
        System.out.println("Amministratore " + getId() + " ha registrato un nuovo dipendente: " + 
                           name + ", " + position + " con ID " + newId);
        return newId;
    }
    
    /**
     * Metodo per aggiornare i dati di un dipendente (simulato).
     * 
     * @param employeeId l'id del dipendente da aggiornare
     * @param field il campo da aggiornare
     * @param value il nuovo valore
     * @return true se l'aggiornamento è avvenuto con successo
     */
    public boolean updateEmployeeData(int employeeId, String field, String value) {
        // Implementazione simulata dell'aggiornamento dei dati di un dipendente
        System.out.println("Amministratore " + getId() + " ha aggiornato il campo '" + 
                           field + "' a '" + value + "' per il dipendente " + employeeId);
        return true;
    }
    
    /**
     * Restituisce una rappresentazione testuale dell'amministratore.
     * 
     * @return una stringa che rappresenta l'amministratore
     */
    @Override
    public String toString() {
        return "EmployeeAdministrator [id=" + getId() + ", accessLevel=" + accessLevel + 
               ", region=" + region + "]";
    }
}

/**
 * Classe con metodo main per testare le classi implementate.
 */
public class EmployeeTest {
    public static void main(String[] args) {
        // Creazione di un dipendente base
        Employee employee = new Employee(1001);
        System.out.println(employee);
        
        // Creazione di un manager
        Manager manager = new Manager(2001, "IT", 10);
        System.out.println(manager);
        manager.assignTask("Sviluppo app mobile", 1001);
        
        // Creazione di un amministratore
        EmployeeAdministrator admin = new EmployeeAdministrator(3001, "COMPLETO", "Europa");
        System.out.println(admin);
        int newEmployeeId = admin.registerEmployee("Mario Rossi", "Sviluppatore");
        admin.updateEmployeeData(newEmployeeId, "indirizzo", "Via Roma 123");
    }
}