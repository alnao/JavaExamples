/** //Ricreare ex-novo a livello logico lo Stack/Pila
 * Implementazione personalizzata di uno Stack (Pila).
 * Segue il principio LIFO (Last In First Out).
 * 
 * @param <T> il tipo di elementi contenuti nello stack
 */
public class CustonPilaStack<T> {
    /**
     * Classe interna che rappresenta un nodo dello stack.
     */
    private class Node {
        T data;      // Il dato contenuto nel nodo
        Node next;   // Riferimento al nodo sottostante nella pila
        
        /**
         * Costruttore per un nodo
         * @param data il dato da inserire nel nodo
         * @param next riferimento al nodo sottostante
         */
        Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }
    
    private Node top;     // Riferimento all'elemento in cima alla pila
    private int size;     // Numero di elementi nella pila
    
    /**
     * Costruttore per uno stack vuoto
     */
    public CustomStack() {
        top = null;
        size = 0;
    }
    
    /**
     * Verifica se lo stack è vuoto
     * @return true se lo stack è vuoto, false altrimenti
     */
    public boolean isEmpty() {
        return top == null;
    }
    
    /**
     * Restituisce il numero di elementi nello stack
     * @return il numero di elementi
     */
    public int size() {
        return size;
    }
    
    /**
     * Inserisce un elemento in cima allo stack (operazione push)
     * @param data l'elemento da inserire
     */
    public void push(T data) {
        // Creiamo un nuovo nodo che punta al nodo attualmente in cima
        Node newNode = new Node(data, top);
        // Il nuovo nodo diventa la cima dello stack
        top = newNode;
        size++;
    }
    
    /**
     * Rimuove e restituisce l'elemento in cima allo stack (operazione pop)
     * @return l'elemento rimosso dalla cima
     * @throws EmptyStackException se lo stack è vuoto
     */
    public T pop() {
        if (isEmpty()) {
            throw new java.util.EmptyStackException();
        }
        
        T data = top.data;    // Salviamo il dato in cima
        top = top.next;       // Aggiorniamo la cima al nodo sottostante
        size--;
        return data;
    }
    
    /**
     * Restituisce l'elemento in cima allo stack senza rimuoverlo (operazione peek)
     * @return l'elemento in cima
     * @throws EmptyStackException se lo stack è vuoto
     */
    public T peek() {
        if (isEmpty()) {
            throw new java.util.EmptyStackException();
        }
        
        return top.data;
    }
    
    /**
     * Svuota lo stack
     */
    public void clear() {
        top = null;
        size = 0;
    }
    
    /**
     * Verifica se lo stack contiene un elemento specifico
     * @param data l'elemento da cercare
     * @return true se l'elemento è presente, false altrimenti
     */
    public boolean contains(T data) {
        Node current = top;
        while (current != null) {
            if (data == null ? current.data == null : data.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    /**
     * Cerca un elemento nello stack e restituisce la sua posizione dalla cima (0-based)
     * @param data l'elemento da cercare
     * @return l'indice dell'elemento (0 è la cima), o -1 se non trovato
     */
    public int search(T data) {
        int index = 0;
        Node current = top;
        
        while (current != null) {
            if (data == null ? current.data == null : data.equals(current.data)) {
                return index;
            }
            current = current.next;
            index++;
        }
        
        return -1;  // Elemento non trovato
    }
    
    /**
     * Restituisce una rappresentazione dello stack in formato stringa
     * @return la rappresentazione in stringa dello stack
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        Node current = top;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("] <- Top");
        return sb.toString();
    }
    
    /**
     * Classe main con esempi di utilizzo
     */
    public static void main(String[] args) {
        CustomStack<String> stack = new CustomStack<>();
        
        // Verifichiamo che lo stack sia inizialmente vuoto
        System.out.println("Lo stack è vuoto? " + stack.isEmpty());
        
        // Aggiungiamo elementi (push)
        stack.push("primo");
        stack.push("secondo");
        stack.push("terzo");
        System.out.println("Stack dopo push: " + stack);
        System.out.println("Dimensione: " + stack.size());
        
        // Visualizziamo l'elemento in cima senza rimuoverlo (peek)
        System.out.println("Elemento in cima (peek): " + stack.peek());
        System.out.println("Stack dopo peek: " + stack);
        
        // Rimuoviamo l'elemento in cima (pop)
        String elementoRimosso = stack.pop();
        System.out.println("Elemento rimosso (pop): " + elementoRimosso);
        System.out.println("Stack dopo pop: " + stack);
        
        // Verifichiamo se lo stack contiene un elemento
        System.out.println("Lo stack contiene 'primo'? " + stack.contains("primo"));
        System.out.println("Lo stack contiene 'terzo'? " + stack.contains("terzo"));
        
        // Cerchiamo la posizione di un elemento
        System.out.println("Posizione di 'primo': " + stack.search("primo"));
        System.out.println("Posizione di 'secondo': " + stack.search("secondo"));
        
        // Svuotiamo lo stack
        stack.clear();
        System.out.println("Stack dopo clear: " + stack);
        
        // Gestiamo l'eccezione se proviamo a fare pop da uno stack vuoto
        try {
            stack.pop();
        } catch (java.util.EmptyStackException e) {
            System.out.println("Eccezione catturata: Impossibile fare pop da uno stack vuoto");
        }
    }
}