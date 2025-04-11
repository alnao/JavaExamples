// Implementazione della classe ListaPersone singleton
import java.util.ArrayList;
import java.util.List;

/*
Capito, creiamo una classe ListaPersone singleton che gestirà una lista di oggetti Persona con nome e cognome. 
Prima definirò la classe Persona standard e poi implementerò la classe ListaPersone usando il pattern Singleton.
Classe ListaPersone SingletonClick to open codeTap to openHo creato due classi:

Una classe Persona semplice con le proprietà nome e cognome
Una classe ListaPersone singleton che gestisce una lista di oggetti Persona

Ecco le caratteristiche principali dell'implementazione:
Classe ListaPersone Singleton:
    public class ListaPersone {
        // Lista di persone
        private List<Persona> persone;
        // Istanza singleton privata e statica
        private static ListaPersone instance;
        // Costruttore privato per impedire l'istanziazione esterna
        private ListaPersone() {
            persone = new ArrayList<>();
        }
        // Metodo pubblico statico per ottenere l'istanza singleton
        public static synchronized ListaPersone getInstance() {
            if (instance == null) {
                instance = new ListaPersone();
            }
            return instance;
        }
    // Ottenere l'istanza singleton
    ListaPersone lista = ListaPersone.getInstance();

Utilizza un'unica istanza condivisa in tutta l'applicazione
    Ha un costruttore privato che impedisce l'istanziazione diretta
    Offre un metodo statico getInstance() thread-safe per accedere all'istanza
    Gestisce internamente una lista di oggetti Persona

Metodi principali:
    aggiungiPersona(): per aggiungere persone alla lista (due versioni)
    rimuoviPersona(): per rimuovere una persona
    getPersone(): restituisce una copia della lista (per proteggere l'originale)
    cercaPersona(): cerca una persona per nome e cognome
    getNumeroPersone(): conteggio delle persone nella lista
    svuotaLista(): rimuove tutte le persone dalla lista
    reset(): metodo statico per resettare l'istanza singleton (utile in fase di test)
    * 
 */


public class Singleton{
    
    public class ListaPersone {
        // Lista di persone
        private List<Persona> persone;
        
        // Istanza singleton privata e statica
        private static ListaPersone instance;
        
        // Costruttore privato per impedire l'istanziazione esterna
        private ListaPersone() {
            persone = new ArrayList<>();
        }
        
        // Metodo pubblico statico per ottenere l'istanza singleton
        public static synchronized ListaPersone getInstance() {
            if (instance == null) {
                instance = new ListaPersone();
            }
            return instance;
        }
        
        // Metodi per gestire la lista di persone
        public void aggiungiPersona(Persona persona) {
            persone.add(persona);
        }
        
        public void aggiungiPersona(String nome, String cognome) {
            persone.add(new Persona(nome, cognome));
        }
        
        public void rimuoviPersona(Persona persona) {
            persone.remove(persona);
        }
        
        public List<Persona> getPersone() {
            return new ArrayList<>(persone); // Restituiamo una copia per proteggere l'originale
        }
        
        public int getNumeroPersone() {
            return persone.size();
        }
        
        public void svuotaLista() {
            persone.clear();
        }
        
        // Metodo per resettare l'istanza singleton (utile per test)
        public static void reset() {
            instance = null;
        }
        
        // Metodo per cercare una persona per nome e cognome
        public Persona cercaPersona(String nome, String cognome) {
            for (Persona p : persone) {
                if (p.getNome().equals(nome) && p.getCognome().equals(cognome)) {
                    return p;
                }
            }
            return null; // Ritorna null se non trovata
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Lista Persone:\n");
            for (Persona p : persone) {
                sb.append("- ").append(p.toString()).append("\n");
            }
            return sb.toString();
        }
    }

    // Definizione della classe Persona standard
    class Persona {
        private String nome;
        private String cognome;
        
        public Persona(String nome, String cognome) {
            this.nome = nome;
            this.cognome = cognome;
        }
        
        public String getNome() {
            return nome;
        }
        
        public void setNome(String nome) {
            this.nome = nome;
        }
        
        public String getCognome() {
            return cognome;
        }
        
        public void setCognome(String cognome) {
            this.cognome = cognome;
        }
        
        @Override
        public String toString() {
            return nome + " " + cognome;
        }
    }
}