//Array 1: 123456. Array 2: 264153. Dimostrare se l’array è ordinato oppure no

public class ArrayChecker {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 3, 4, 5, 6};
        int[] array2 = {2, 6, 4, 1, 5, 3};
        System.out.println("Array 1: " + (isOrdered(array1) ? "Ordinato" : "Non ordinato"));
        System.out.println("Array 2: " + (isOrdered(array2) ? "Ordinato" : "Non ordinato"));
    }

    public static boolean isOrdered(int[] array) {
        if (array == null || array.length <= 1) {
            return true; // Array vuoto o con un solo elemento è sempre ordinato
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }    
}
