//Resturant reservation: each reservation contains name,number,notes. implement adding,remove e retrieving reservations.
//reservation should be returned sorted by name. la disponiblità dei tavoli, cioè una collezione di tavoli e riserve, considera che ogni tavolo ha un numero massimo di posti
import java.util.*;
public class Resturant {
    public boolean addReservation(String customerName, int numberOfPeople, String notes) {
        // Cerca un tavolo disponibile con capacità sufficiente
        Table availableTable = findAvailableTable(numberOfPeople);        
        if (availableTable != null) {
            Reservation reservation = new Reservation(customerName, numberOfPeople, notes);
            reservation.setAssignedTable(availableTable);
            availableTable.setOccupied(true);
            reservations.add(reservation);
            Collections.sort(reservations); // Mantieni le prenotazioni ordinate per nome
            return true;
        }        
        return false; // Nessun tavolo disponibile
    }

    private Table findAvailableTable(int numberOfPeople) {
        // Trova il tavolo più piccolo che può ospitare il gruppo
        Table bestTable = null;
        int minExtraCapacity = Integer.MAX_VALUE;
        for (Table table : tables) {
            if (table.getCapacity() >= numberOfPeople && !table.isOccupied()) {
                int extraCapacity = table.getCapacity() - numberOfPeople;
                if (extraCapacity < minExtraCapacity) {
                    minExtraCapacity = extraCapacity;
                    bestTable = table;
                }
            }
        }
        return bestTable;
    }

    // Classe per rappresentare una prenotazione
    static class Reservation implements Comparable<Reservation> {
        private String customerName;
        private int numberOfPeople;
        private String notes;
        private Table assignedTable;
    
        public Reservation(String customerName, int numberOfPeople, String notes) {
            this.customerName = customerName;
            this.numberOfPeople = numberOfPeople;
            this.notes = notes;
        }
    
        public String getCustomerName() {
            return customerName;
        }
    
        public int getNumberOfPeople() {
            return numberOfPeople;
        }
    
        public String getNotes() {
            return notes;
        }
    
        public Table getAssignedTable() {
            return assignedTable;
        }
    
        public void setAssignedTable(Table table) {
            this.assignedTable = table;
        }
    
        @Override
        public int compareTo(Reservation other) {
            return this.customerName.compareToIgnoreCase(other.customerName);
        }
    
        @Override
        public String toString() {
            return String.format("Prenotazione: %s, %d persone, Tavolo: %s, Note: %s",
                    customerName, numberOfPeople, 
                    (assignedTable != null) ? assignedTable.getTableNumber() : "Non assegnato",
                    notes);
        }
    }
    
    // Classe per rappresentare un tavolo
    class Table {
        private int tableNumber;
        private int capacity;
        private boolean occupied;
    
        public Table(int tableNumber, int capacity) {
            this.tableNumber = tableNumber;
            this.capacity = capacity;
            this.occupied = false;
        }
    
        public int getTableNumber() {
            return tableNumber;
        }
    
        public int getCapacity() {
            return capacity;
        }
        
        public boolean isOccupied() {
            return occupied;
        }
        
        public void setOccupied(boolean occupied) {
            this.occupied = occupied;
        }
    }
    
    // class RestaurantReservationSystem{
        private List<Table> tables;
        private List<Reservation> reservations;
    
        public Resturant() {//ex RestaurantReservationSystem
            this.tables = new ArrayList<>();
            this.reservations = new ArrayList<>();
        }
    
        public void addTable(int tableNumber, int capacity) {
            tables.add(new Table(tableNumber, capacity));
        }
    
 
    
        public boolean removeReservation(String customerName) {
            Reservation toRemove = null;
            
            for (Reservation reservation : reservations) {
                if (reservation.getCustomerName().equalsIgnoreCase(customerName)) {
                    toRemove = reservation;
                    break;
                }
            }
            
            if (toRemove != null) {
                Table table = toRemove.getAssignedTable();
                if (table != null) {
                    table.setOccupied(false);
                }
                reservations.remove(toRemove);
                return true;
            }
            
            return false;
        }
    
        public List<Reservation> getAllReservations() {
            // Ritorna una copia della lista per evitare modifiche esterne
            return new ArrayList<>(reservations);
        }
    
        public List<Table> getAvailableTables(int numberOfPeople) {
            List<Table> availableTables = new ArrayList<>();
            
            for (Table table : tables) {
                if (table.getCapacity() >= numberOfPeople && !table.isOccupied()) {
                    availableTables.add(table);
                }
            }
            
            return availableTables;
        }
    
        // Metodo main per testare il sistema
        public static void main(String[] args) {
            Resturant system = new Resturant();
            
            // Aggiungi tavoli al ristorante
            system.addTable(1, 2);  // Tavolo per 2 persone
            system.addTable(2, 4);  // Tavolo per 4 persone
            system.addTable(3, 4);  // Tavolo per 4 persone
            system.addTable(4, 6);  // Tavolo per 6 persone
            system.addTable(5, 8);  // Tavolo per 8 persone
            
            // Crea alcune prenotazioni
            System.out.println("Aggiunta prenotazioni:");
            System.out.println("Rossi (4 persone): " + 
                (system.addReservation("Rossi", 4, "Vicino alla finestra") ? "Confermata" : "Non disponibile"));
            
            System.out.println("Bianchi (2 persone): " + 
                (system.addReservation("Bianchi", 2, "Compleanno") ? "Confermata" : "Non disponibile"));
            
            System.out.println("Verdi (6 persone): " + 
                (system.addReservation("Verdi", 6, "") ? "Confermata" : "Non disponibile"));
            
            System.out.println("Neri (8 persone): " + 
                (system.addReservation("Neri", 8, "Menu vegetariano") ? "Confermata" : "Non disponibile"));
            
            // Prova ad aggiungere una prenotazione per un tavolo che è già pieno
            System.out.println("Blu (4 persone): " + 
                (system.addReservation("Blu", 4, "") ? "Confermata" : "Non disponibile"));
            
            // Visualizza tutte le prenotazioni (ordinate per nome)
            System.out.println("\nTutte le prenotazioni:");
            for (Reservation r : system.getAllReservations()) {
                System.out.println(r);
            }
            
            // Rimuovi una prenotazione
            System.out.println("\nRimozione della prenotazione di Rossi: " + 
                (system.removeReservation("Rossi") ? "Rimossa" : "Non trovata"));
            
            // Visualizza di nuovo tutte le prenotazioni
            System.out.println("\nTutte le prenotazioni dopo la rimozione:");
            for (Reservation r : system.getAllReservations()) {
                System.out.println(r);
            }
            
            // Verifica tavoli disponibili per una nuova prenotazione
            System.out.println("\nTavoli disponibili per 4 persone:");
            List<Table> availableTables = system.getAvailableTables(4);
            if (availableTables.isEmpty()) {
                System.out.println("Nessun tavolo disponibile");
            } else {
                for (Table t : availableTables) {
                    System.out.println("Tavolo " + t.getTableNumber() + " (capacità: " + t.getCapacity() + ")");
                }
            }
            
            // Prova ad aggiungere una nuova prenotazione dopo aver liberato un tavolo
            System.out.println("\nAggiunta di una nuova prenotazione:");
            System.out.println("Blu (4 persone): " + 
                (system.addReservation("Blu", 4, "Tavolo riservato") ? "Confermata" : "Non disponibile"));
                
            // Visualizza le prenotazioni finali
            System.out.println("\nPrenotazioni finali:");
            for (Reservation r : system.getAllReservations()) {
                System.out.println(r);
            }
        }
}
