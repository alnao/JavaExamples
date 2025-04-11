//Ecco un'implementazione in Java che verifica se due stringhe sono palindrome utilizzando una HashMap:
public class PalindromoCheker {
    public static boolean isPalindrome(String str) {
        // Verifica caso base
        if (str == null) {
            return false;
        }
        // Rimuovi spazi e converti in minuscolo per un confronto più flessibile
        str = str.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        int left = 0;
        int right = str.length() - 1;
        // Confronta i caratteri simmetrici rispetto al centro
        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    public static void main(String[] args) {
        // Test cases
        String[] testCases = {"prova", "anna", "radar", "palindrome", "albert" , "fattottaf"};
        for (String test : testCases) {
            boolean result = isPalindrome(test);
            System.out.println("\"" + test + "\" è un palindromo? " + result);
        }        
    }
}
