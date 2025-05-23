/*
 SINGLETON

     static class Universita {
        private String nome;
        private List<Studente> studenti;
        private List<Docente> docenti;
        
        // Istanza singleton
        private static Universita instance;
        
        // Costruttore privato per impedire l'istanziazione esterna
        private Universita(String nome) {
            this.nome = nome;
            this.studenti = new ArrayList<>();
            this.docenti = new ArrayList<>();
        }
        
        // Metodo statico per ottenere l'istanza singleton
        public static synchronized Universita getInstance(String nome) {
            if (instance == null) {
                instance = new Universita(nome);
            }
            return instance;
        }
        
///
    Universita uniPd = Universita.getInstance("Università di Padova");

 * 
 */


import java.util.ArrayList;
import java.util.List;

public class GestioneUniversitariaSingleton {
    interface IPersona{
        public String getInfo();
    }

    static abstract class Persona implements IPersona {
        private String nome;
        private String cognome;
        public Persona(String nome, String cognome) {
            this.nome = nome;
            this.cognome = cognome;
        }
        public String getNome(){return this.nome;}
        public String getCognome(){return this.cognome;}
        public String getInfo() {
            return this.nome + " " + this.cognome;
        }
    }
    // Classe Studente che eredita da Persona
    static class Studente extends Persona {
        private String matricola;
        private String universita;
        public Studente(String nome, String cognome, String matricola, String universita) {
            super(nome, cognome);
            this.matricola = matricola;
            this.universita = universita;
        }
        public String getMatricola() {
            return matricola;
        }
        public String getUniversita() {
            return universita;
        }
        @Override
        public String getInfo() {
            return super.getInfo() + ", Matricola: " + matricola;
        }
    }
    // Classe Docente che eredita da Persona
    static class Docente extends Persona {
        private String materia;
        private double salario;
        public Docente(String nome, String cognome, String materia, double salario) {
            super(nome, cognome);
            this.materia = materia;
            this.salario = salario;
        }
        public Double getSalario(){return this.salario;}
        public String getMateria(){return this.materia;}
        @Override
        public String getInfo() {
            return super.getInfo() + ", materia: " + materia;
        }
    }
    
    // Classe Università trasformata in Singleton
    static class Universita {
        private String nome;
        private List<Studente> studenti;
        private List<Docente> docenti;
        
        // Istanza singleton
        private static Universita instance;
        
        // Costruttore privato per impedire l'istanziazione esterna
        private Universita(String nome) {
            this.nome = nome;
            this.studenti = new ArrayList<>();
            this.docenti = new ArrayList<>();
        }
        
        // Metodo statico per ottenere l'istanza singleton
        public static synchronized Universita getInstance(String nome) {
            if (instance == null) {
                instance = new Universita(nome);
            }
            return instance;
        }
        
        // Metodo per ottenere il nome dell'università
        public String getNome() {
            return this.nome;
        }
        
        public void aggiungiStudente(Studente studente) {
            studenti.add(studente);
        }
        
        public void aggiungiDocente(Docente docente) {
            docenti.add(docente);
        }
        
        public void stampaPersone() {
            System.out.println("Persone dell'Università " + nome + ":");
            System.out.println("\nDocenti:");
            for (Docente docente : docenti) {
                System.out.println(docente.getInfo());
            }

            System.out.println("\nStudenti:");
            for (Studente studente : studenti) {
                System.out.println(studente.getInfo());
            }
        }
        
        public Docente getDocenteConSalarioPiuAlto() {
            if (docenti.isEmpty()) {
                return null;
            }
            Docente docenteMaxSalario = docenti.get(0);
            for (Docente docente : docenti) {
                if (docente.getSalario() > docenteMaxSalario.getSalario()) {
                    docenteMaxSalario = docente;
                }
            }
            return docenteMaxSalario;
        }
        
        // Metodo per creare un array di tutte le persone (studenti e docenti)
        public List<Persona> getElencoPersone() {
            List<Persona> persone = new ArrayList<>();
            // Aggiungi tutti i docenti (sono anche persone)
            persone.addAll(docenti);
            // Aggiungi tutti gli studenti (sono anche persone)
            persone.addAll(studenti);
            return persone;
        }
        
        // Metodo per stampare tutte le persone
        public void stampaElencoPersone() {
            List<Persona> persone = getElencoPersone();
            System.out.println("Elenco completo delle persone nell'Università " + nome + ":");
            for (Persona persona : persone) {
                System.out.println(persona.getInfo());
            }
        }
        
        // Metodo per resettare il singleton (utile per test)
        public static void reset() {
            instance = null;
        }
    }

    public static void main(String[] args) {
        // Creazione università usando il pattern singleton
        Universita uniPd = Universita.getInstance("Università di Padova");

        // Aggiunta docenti
        uniPd.aggiungiDocente(new Docente("Mario", "Rossi", "Matematica", 45000.0));
        uniPd.aggiungiDocente(new Docente("Laura", "Bianchi", "Fisica", 52000.0));
        uniPd.aggiungiDocente(new Docente("Giovanni", "Verdi", "Informatica", 48000.0));

        // Aggiunta studenti
        uniPd.aggiungiStudente(new Studente("Andrea", "Neri", "S123456", "Università di Padova"));
        uniPd.aggiungiStudente(new Studente("Paola", "Gialli", "S789012", "Università di Padova"));
        uniPd.aggiungiStudente(new Studente("Marco", "Blu", "S345678", "Università di Padova"));

        // Stampa tutte le persone
        uniPd.stampaPersone();

        // Trova e stampa il docente con il salario più alto
        Docente docenteMaxSalario = uniPd.getDocenteConSalarioPiuAlto();
        System.out.println("\nDocente con il salario più alto:");
        if (docenteMaxSalario != null) {
            System.out.println(docenteMaxSalario.getInfo());
        } else {
            System.out.println("Nessun docente trovato.");
        }

        // Stampa l'elenco completo di persone
        System.out.println("\nElenco completo:");
        uniPd.stampaElencoPersone();
        
        // Esempio per dimostrare che è un singleton
        System.out.println("\nDimostrazione del pattern Singleton:");
        Universita altraIstanza = Universita.getInstance("Università di Milano");
        System.out.println("Nome dell'università dall'altra istanza: " + altraIstanza.getNome());
        System.out.println("Le due istanze sono uguali? " + (uniPd == altraIstanza));
    }
}