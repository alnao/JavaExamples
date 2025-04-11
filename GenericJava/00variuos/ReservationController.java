public class RestaurantReservationSystem {
    private List<Table> tables;
    private List<Reservation> reservations;
    // Istanza singleton
    private static RestaurantReservationSystem instance;
    // Costruttore privato
    private RestaurantReservationSystem() {
        this.tables = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }
    // Metodo per ottenere l'istanza
    public static synchronized RestaurantReservationSystem getInstance() {
        if (instance == null) {
            instance = new RestaurantReservationSystem();
        }
        return instance;
    }
    
    // Il resto della classe rimane invariato...
}

public static void main(String[] args) {
    // Ottieni l'istanza singleton invece di creare un nuovo oggetto
    RestaurantReservationSystem system = RestaurantReservationSystem.getInstance();
    
    // Il resto del codice rimane invariato...
}

//Aggiungi l'annotazione @Service (o @Component, @Repository, ecc. a seconda del ruolo della classe)
//Non è necessario mantenere una variabile statica instance  ,      Non è necessario il metodo getInstance()    , Il costruttore può essere pubblico
import org.springframework.stereotype.Service;

@Service
public class RestaurantReservationSystem {
    private List<Table> tables;
    private List<Reservation> reservations;
    
    // Il costruttore diventa pubblico
    public RestaurantReservationSystem() {
        this.tables = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }
    
    // Non è necessario il pattern singleton manuale
    // Spring gestirà automaticamente l'istanza
    
    // Il resto dei metodi rimane invariato...
}


@RestController
public class ReservationController {
    private final RestaurantReservationSystem reservationSystem;
    
    // Iniezione delle dipendenze tramite costruttore
    public ReservationController(RestaurantReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
    }
    
    // Esempio di endpoint REST
    @PostMapping("/reservations")
    public ResponseEntity<String> addReservation(@RequestBody ReservationRequest request) {
        boolean success = reservationSystem.addReservation(
            request.getCustomerName(),
            request.getNumberOfPeople(),
            request.getNotes()
        );
        
        if (success) {
            return ResponseEntity.ok("Prenotazione confermata");
        } else {
            return ResponseEntity.badRequest().body("Nessun tavolo disponibile");
        }
    }
    
    // Altri endpoint...
}
