import java.util.*;

public class EmployeeAdvanced {
    // Classe astratta Employee
    static public abstract class Employee {
        // Attributo baseRank
        private double baseRank;
        
        // Costruttore che riceve baseRank
        public Employee(double baseRank) {
            this.baseRank = baseRank;
        }
        
        // Metodo astratto getRankMultiplier
        public abstract double getRankMultiplier();
        
        // Metodo concreto getRank
        public double getRank() {
            return baseRank * getRankMultiplier();
        }
    }

    // Classe Manager che estende Employee
    static public class Manager extends Employee {
        // Attributi specifici del Manager
        private int teamSize;
        
        // Costruttore
        public Manager(double baseRank, int teamSize) {
            super(baseRank);
            this.teamSize = teamSize;
        }
        
        // Implementazione del metodo astratto
        @Override
        public double getRankMultiplier() {
            // I manager hanno un moltiplicatore che dipende dalla dimensione del team
            return 2.0 + (teamSize * 0.1);
        }
        
        // Getter per teamSize
        public int getTeamSize() {
            return teamSize;
        }
        
        // Setter per teamSize
        public void setTeamSize(int teamSize) {
            this.teamSize = teamSize;
        }
    }

    // Classe Developer che estende Employee
    static public class Developer extends Employee {
        // Attributi specifici del Developer
        private int yearsOfExperience;
        private String specialization;
        
        // Costruttore
        public Developer(double baseRank, int yearsOfExperience, String specialization) {
            super(baseRank);
            this.yearsOfExperience = yearsOfExperience;
            this.specialization = specialization;
        }
        
        // Implementazione del metodo astratto
        @Override
        public double getRankMultiplier() {
            // I developer hanno un moltiplicatore basato sugli anni di esperienza
            return 1.0 + (yearsOfExperience * 0.05);
        }
        
        // Getter per yearsOfExperience
        public int getYearsOfExperience() {
            return yearsOfExperience;
        }
        
        // Setter per yearsOfExperience
        public void setYearsOfExperience(int yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
        }
        
        // Getter per specialization
        public String getSpecialization() {
            return specialization;
        }
        
        // Setter per specialization
        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }
    }

    // Classe di esempio per testare la gerarchia

    public static void main(String[] args) {
        // Creazione di un Manager con baseRank 1000 e team di 5 persone
        Manager manager = new Manager(1000, 5);
        
        // Creazione di un Developer con baseRank 800, 3 anni di esperienza e specializzazione Java
        Developer developer = new Developer(800, 3, "Java");
        
        // Calcolo e stampa dei rank
        System.out.println("Manager Rank: " + manager.getRank());
        System.out.println("Developer Rank: " + developer.getRank());
    }

}
