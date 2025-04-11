//Array: «dog cat cat cow cow cow». Ricavare il numero di occorrenze per ogni elemento
import java.util.*;

public class ArrayGroupBy {
    public static void main(String[] args) {
        String[] array = {"dog", "cat", "cat", "cow", "cow", "cow"};
        Map<String, Integer> map = new HashMap<>();
        for (String s : array) {
            map.put(s, map.getOrDefault(s, 0) + 1);
        }
        System.out.println(map);
    }
}
