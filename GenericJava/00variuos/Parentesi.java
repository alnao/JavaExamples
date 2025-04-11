//in java: definisci una classe BracketObj per identificare un oggetto con uno solo attributo immutabile che rappresenti una parentesi valida, 
// la lista delle parentesi valide sono { } [ ] ( ). Fornire una implementazione della interfaccia BackerValidator per verificare se l'array di parentesi 
//passato in input rappresenti una sequenza valida di parentesi bilanciatre. 
// Esempio valido "{{()()}()}", esempio non valido "{[}()}". 
// dammi anche un test auotmatico per il metodo isvalid.
import java.util.*;
public class Parentesi {
    static  class BracketValidatorImpl implements BracketValidator {
        private static final Map<Character, Character> BRACKETS_MAP = new HashMap<>();
        static {
            BRACKETS_MAP.put('}', '{');
            BRACKETS_MAP.put(']', '[');
            BRACKETS_MAP.put(')', '(');
        }
        @Override
        public boolean isValid(BracketObj[] brackets) {
            if (brackets == null) {
                return true; // Una sequenza vuota è considerata bilanciata
            }
            Stack<Character> stack = new Stack<>();
            for (BracketObj bracketObj : brackets) {
                char bracket = bracketObj.getBracket();
                if (bracketObj.isOpeningBracket()) {
                    stack.push(bracket);
                } else {// È una parentesi di chiusura
                    if (stack.isEmpty()) {
                        return false; // Non c'è una parentesi di apertura corrispondente
                    }
                    char lastOpenBracket = stack.pop();
                    if (lastOpenBracket != BRACKETS_MAP.get(bracket)) {
                        return false; 
                    }
                }
            }
            return stack.isEmpty(); // La sequenza è bilanciata se lo stack è vuoto
        }
        public boolean isValid(String brackets) {
            if (brackets == null || brackets.isEmpty()) {
                return true; // Una sequenza vuota è considerata bilanciata
            }
            
            // Converti la stringa in un array di BracketObj
            BracketObj[] bracketObjArray = new BracketObj[brackets.length()];
            for (int i = 0; i < brackets.length(); i++) {
                bracketObjArray[i] = new BracketObj(brackets.charAt(i));
            }
            
            // Richiama il metodo che lavora su array di BracketObj
            return isValid(bracketObjArray);
        }
    }

    static public final class BracketObj {
        private final char bracket;
        public BracketObj(char bracket) {
            if (bracket != '{' && bracket != '}' && 
                bracket != '[' && bracket != ']' && 
                bracket != '(' && bracket != ')') {
                throw new IllegalArgumentException("Carattere non valido: " + bracket);
            }
            this.bracket = bracket;
        }
        public char getBracket() {
            return bracket;
        }
        public boolean isOpeningBracket() {
            return bracket == '{' || bracket == '[' || bracket == '(';
        }
        public char getMatchingClosingBracket() {
            if (!isOpeningBracket()) {
                throw new IllegalStateException("Non è una parentesi di apertura: " + bracket);
            }
            
            switch (bracket) {
                case '{': return '}';
                case '[': return ']';
                case '(': return ')';
                default: throw new IllegalStateException("Parentesi non riconosciuta");
            }
        }        
        @Override
        public String toString() {            return String.valueOf(bracket);        }
        
        @Override
        public boolean equals(Object obj) {             
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BracketObj that = (BracketObj) obj;
            return bracket == that.bracket;
        }
        @Override
        public int hashCode() {
            return Character.hashCode(bracket);
        }
    }
    interface BracketValidator {
        boolean isValid(BracketObj[] brackets);

    }


    public static void main(String[] args) {
        BracketValidatorImpl o=new BracketValidatorImpl();
        String stringa1="{{()()}()}";
        boolean result1=o.isValid ( stringa1 );
        System.out.println( stringa1 + (result1 ? " parentesi bilanciate" : " parentesi non bilanciate") );
        String stringa2="{[}()}";
        boolean result2=o.isValid ( stringa2 );
        System.out.println( stringa2 + (result2 ? " parentesi bilanciate" : " parentesi non bilanciate") );
    }
}
