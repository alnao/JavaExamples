//definire una classe immutabile (importante che sia immutabile) MyObject avente unico valore 
//"value" di tipo String con getter. definire classe AnagramChecker con metodo per verificare se due oggetti MyObject sono anagrammi. 
//Fornire anche una classe di test.

public class MyObjectImmutable {
    // Classe immutabile MyObject
    static public final class MyObject {
        private final String value;
        public MyObject(String value) {
            this.value = value != null ? value : "";
        }
        public String getValue() {
            return value;
        }
        @Override
        public String toString() {
            return "MyObject[value=" + value + "]";
        }
    }
    // Classe AnagramChecker per verificare se due MyObject sono anagrammi
    static public class AnagramChecker {
        public static boolean areAnagrams(MyObject obj1, MyObject obj2) {
            // Verifica parametri nulli
            if (obj1 == null || obj2 == null) {
                return false;
            }

            String str1 = obj1.getValue();
            String str2 = obj2.getValue();

            // Se le lunghezze sono diverse, non possono essere anagrammi
            if (str1.length() != str2.length()) {
                return false;
            }

            // Utilizza un array di conteggio dei caratteri
            int[] charCount = new int[256]; // Assumiamo ASCII

            // Incrementa il conteggio per ogni carattere nella prima stringa
            for (char c : str1.toCharArray()) {
                charCount[c]++;
            }

            // Decrementa il conteggio per ogni carattere nella seconda stringa
            for (char c : str2.toCharArray()) {
                charCount[c]--;
                // Se troviamo un carattere non presente nella prima stringa
                if (charCount[c] < 0) {
                    return false;
                }
            }

            // Tutte le occorrenze si sono annullate, quindi sono anagrammi
            return true;
        }
    }

    // Classe di test

    public static void main(String[] args) {
        // Test case 1: anagrammi
        MyObject obj1 = new MyObject("listen");
        MyObject obj2 = new MyObject("silent");
        System.out.println("Test 1 - '" + obj1.getValue() + "' e '" + obj2.getValue() +
                        "' sono anagrammi: " + AnagramChecker.areAnagrams(obj1, obj2));

        // Test case 2: non anagrammi
        MyObject obj3 = new MyObject("hello");
        MyObject obj4 = new MyObject("world");
        System.out.println("Test 2 - '" + obj3.getValue() + "' e '" + obj4.getValue() +
                        "' sono anagrammi: " + AnagramChecker.areAnagrams(obj3, obj4));

        // Test case 3: stessa parola
        MyObject obj5 = new MyObject("test");
        MyObject obj6 = new MyObject("test");
        System.out.println("Test 3 - '" + obj5.getValue() + "' e '" + obj6.getValue() +
                        "' sono anagrammi: " + AnagramChecker.areAnagrams(obj5, obj6));

        // Test case 4: case sensitive
        MyObject obj7 = new MyObject("State");
        MyObject obj8 = new MyObject("Taste");
        System.out.println("Test 4 - '" + obj7.getValue() + "' e '" + obj8.getValue() +
                        "' sono anagrammi: " + AnagramChecker.areAnagrams(obj7, obj8));

        // Test case 5: spazi
        MyObject obj9 = new MyObject("rail safety");
        MyObject obj10 = new MyObject("fairy tales");
        System.out.println("Test 5 - '" + obj9.getValue() + "' e '" + obj10.getValue() +
                        "' sono anagrammi: " + AnagramChecker.areAnagrams(obj9, obj10));


    }
}
/*
 * 
    @Test
    @DisplayName("Should return true for MyObjects with anagram values")
    void shouldReturnTrueForAnagrams() {
        MyObject obj1 = new MyObject("listen");
        MyObject obj2 = new MyObject("silent");
        assertTrue(AnagramChecker.areAnagrams(obj1, obj2));
    }
 * 
 */