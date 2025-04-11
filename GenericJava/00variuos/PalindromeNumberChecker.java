//sempre in java, creami una classe e un metodo che mi indica se una stringa contiene solo numeri palindromi, 
//esempio "1 7 33 99" è ok, esempio "1 23 99" non è palindromo
public class PalindromeNumberChecker {
    public static boolean containsOnlyPalindromeNumbers(String input) {
        if (input == null || input.trim().isEmpty()) {// Gestione caso null o stringa vuota
            return true; // Per convenzione, consideriamo una stringa vuota come valida
        }
        String[] numbers = input.trim().split(" "); // Dividiamo la stringa in token separati da spazi
        for (String number : numbers) {// Controlliamo che ogni token sia un numero palindromo
            try {// Verifichiamo che sia un numero
                Integer.parseInt(number);
                if (!isPalindrome(number)) {// Verifichiamo che sia palindromo
                    return false;
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Input non valido: '" + number + "' non è un numero");
            }
        }
        return true;
    }
    private static boolean isPalindrome(String str) {
        int left = 0;
        int right = str.length() - 1;
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
        // Test con stringhe valide (contengono solo numeri palindromi)
        testAndPrint("1 7 33 99");
        testAndPrint("121 1331 1221");
        testAndPrint("9");
        testAndPrint("");  // stringa vuota

        // Test con stringhe non valide (contengono numeri non palindromi)
        testAndPrint("1 23 99");
        testAndPrint("12345");
        testAndPrint("121 123 121");

        // Test con input non validi
        try {
            testAndPrint("1 abc 33");
        } catch (NumberFormatException e) {
            System.out.println("Eccezione catturata come previsto: " + e.getMessage());
        }
    }

    /**
     * Utility per testare e stampare il risultato.
     */
    private static void testAndPrint(String input) {
        boolean result = containsOnlyPalindromeNumbers(input);
        System.out.println("\"" + input + "\" contiene solo numeri palindromi? " + result);
    }
}
