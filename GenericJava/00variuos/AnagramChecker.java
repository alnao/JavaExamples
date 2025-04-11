import java.util.HashMap;
import java.util.Map;
public class AnagramChecker {
    public static boolean areAnagrams(String str1, String str2) {
        // Verifica casi base
        if (str1 == null || str2 == null) {
            return false;
        }
        // Rimuovi spazi e converti in minuscolo per un confronto più flessibile
        str1 = str1.replaceAll("\\s", "").toLowerCase();
        str2 = str2.replaceAll("\\s", "").toLowerCase();
        // Se le lunghezze sono diverse, non possono essere anagrammi
        if (str1.length() != str2.length()) {
            return false;
        }
        // Usa una HashMap per contare i caratteri della prima stringa
        Map<Character, Integer> charCountMap = new HashMap<>();
        // Incrementa il conteggio per ogni carattere della prima stringa
        for (char c : str1.toCharArray()) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }
        // Decrementa il conteggio per ogni carattere della seconda stringa
        for (char c : str2.toCharArray()) {
            // Se il carattere non esiste nella mappa o ha conteggio 0, non è un anagramma
            if (!charCountMap.containsKey(c) || charCountMap.get(c) == 0) {
                return false;
            }
            charCountMap.put(c, charCountMap.get(c) - 1);
        }
        // Se tutti i conteggi sono stati decrementati correttamente, le stringhe sono anagrammi
        return true;
    }
    public static void main(String[] args) {
        // Test cases
        String[][] testCases = {
            {"listen", "silent"},             // anagrammi
            {"hello", "hello"},               // stessa parola
            {"triangle", "integral"},         // anagrammi
            {"conversation", "conservation"}, // anagrammi
            {"hello", "world"},               // non anagrammi
            {"abc", "abcd"},                  // lunghezze diverse
            {"rail safety", "fairy tales"},   // anagrammi con spazi
            {"William Shakespeare", "I am a weakish speller"}, // anagrammi famosi
            {"", ""},                         // stringhe vuote
            {"a", "a"}                        // singolo carattere
        };

        for (String[] test : testCases) {
            boolean result = areAnagrams(test[0], test[1]);
            System.out.println("\"" + test[0] + "\" e \"" + test[1] + "\" sono anagrammi? " + result);
        }
    }
}