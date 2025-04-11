//Implementare un albero n-ario
//  setRoot
//      public Node addChild(Node parent, T childData) {
//      public boolean removeNode(Node node) {


import java.util.*;

/**
 * Implementazione di un albero n-ario, dove ogni nodo può avere un numero
 * arbitrario di figli.
 * 
 * @param <T> il tipo di dati contenuti nei nodi dell'albero
 */
public class AlberoN<T> {
    
    /**
     * Classe interna che rappresenta un nodo dell'albero n-ario.
     */
    public class Node {
        private T data;                  // Dato contenuto nel nodo
        private List<Node> children;     // Lista dei nodi figli
        private Node parent;             // Riferimento al nodo genitore
        
        /**
         * Costruttore per un nodo con valore specificato.
         * 
         * @param data il valore da inserire nel nodo
         */
        public Node(T data) {
            this.data = data;
            this.children = new ArrayList<>();
            this.parent = null;
        }
        
        /**
         * Restituisce il valore contenuto nel nodo.
         * 
         * @return il valore del nodo
         */
        public T getData() {
            return data;
        }
        
        /**
         * Imposta il valore contenuto nel nodo.
         * 
         * @param data il nuovo valore del nodo
         */
        public void setData(T data) {
            this.data = data;
        }
        
        /**
         * Restituisce la lista dei nodi figli.
         * 
         * @return lista dei figli
         */
        public List<Node> getChildren() {
            return children;
        }
        
        /**
         * Aggiunge un figlio a questo nodo.
         * 
         * @param child il nodo figlio da aggiungere
         */
        public void addChild(Node child) {
            child.parent = this;
            children.add(child);
        }
        
        /**
         * Rimuove un figlio da questo nodo.
         * 
         * @param child il nodo figlio da rimuovere
         * @return true se il figlio è stato rimosso, false altrimenti
         */
        public boolean removeChild(Node child) {
            boolean removed = children.remove(child);
            if (removed) {
                child.parent = null;
            }
            return removed;
        }
        
        /**
         * Restituisce il nodo genitore.
         * 
         * @return il nodo genitore o null se è la radice
         */
        public Node getParent() {
            return parent;
        }
        
        /**
         * Verifica se il nodo è una foglia (non ha figli).
         * 
         * @return true se il nodo è una foglia, false altrimenti
         */
        public boolean isLeaf() {
            return children.isEmpty();
        }
        
        /**
         * Restituisce il numero di figli di questo nodo.
         * 
         * @return il numero di figli
         */
        public int getChildCount() {
            return children.size();
        }
        
        @Override
        public String toString() {
            return data.toString();
        }
    }
    
    private Node root;    // La radice dell'albero
    private int size;     // Numero totale di nodi nell'albero
    
    /**
     * Costruttore per un albero vuoto.
     */
    public NaryTree() {
        root = null;
        size = 0;
    }
    
    /**
     * Costruttore per un albero con radice specificata.
     * 
     * @param rootData il valore da inserire nella radice
     */
    public NaryTree(T rootData) {
        this.root = new Node(rootData);
        size = 1;
    }
    
    /**
     * Restituisce il nodo radice dell'albero.
     * 
     * @return il nodo radice o null se l'albero è vuoto
     */
    public Node getRoot() {
        return root;
    }
    
    /**
     * Imposta la radice dell'albero.
     * 
     * @param data il valore da inserire nella radice
     */
    public void setRoot(T data) {
        if (root == null) {
            root = new Node(data);
            size = 1;
        } else {
            root.data = data;
        }
    }
    
    /**
     * Verifica se l'albero è vuoto.
     * 
     * @return true se l'albero è vuoto, false altrimenti
     */
    public boolean isEmpty() {
        return root == null;
    }
    
    /**
     * Restituisce il numero di nodi nell'albero.
     * 
     * @return il numero di nodi
     */
    public int size() {
        return size;
    }
    
    /**
     * Svuota l'albero.
     */
    public void clear() {
        root = null;
        size = 0;
    }
    
    /**
     * Aggiunge un nodo figlio a un nodo specificato.
     * 
     * @param parent il nodo genitore
     * @param childData il valore da inserire nel nuovo nodo figlio
     * @return il nodo figlio creato o null se il genitore non è valido
     */
    public Node addChild(Node parent, T childData) {
        if (parent == null) {
            return null;
        }
        
        Node child = new Node(childData);
        parent.addChild(child);
        size++;
        return child;
    }
    
    /**
     * Rimuove un nodo dall'albero.
     * 
     * @param node il nodo da rimuovere
     * @return true se il nodo è stato rimosso, false altrimenti
     */
    public boolean removeNode(Node node) {
        if (node == null) {
            return false;
        }
        
        if (node == root) {
            root = null;
            size = 0;
            return true;
        }
        
        Node parent = node.getParent();
        if (parent != null) {
            int removedNodes = countNodes(node);
            boolean removed = parent.removeChild(node);
            if (removed) {
                size -= removedNodes;
            }
            return removed;
        }
        
        return false;
    }
    
    /**
     * Conta il numero di nodi nel sottoalbero radicato nel nodo specificato.
     * 
     * @param node la radice del sottoalbero
     * @return il numero di nodi nel sottoalbero
     */
    private int countNodes(Node node) {
        if (node == null) {
            return 0;
        }
        
        int count = 1; // Contiamo il nodo corrente
        for (Node child : node.getChildren()) {
            count += countNodes(child);
        }
        return count;
    }
    
    /**
     * Esegue una visita in profondità (Depth-First Search) dell'albero.
     * 
     * @return lista dei valori dei nodi nell'ordine di visita
     */
    public List<T> depthFirstTraversal() {
        List<T> result = new ArrayList<>();
        dfsHelper(root, result);
        return result;
    }
    
    /**
     * Funzione di supporto per la visita in profondità.
     * 
     * @param node il nodo corrente
     * @param result la lista dei risultati
     */
    private void dfsHelper(Node node, List<T> result) {
        if (node == null) {
            return;
        }
        
        result.add(node.getData());
        for (Node child : node.getChildren()) {
            dfsHelper(child, result);
        }
    }
    
    /**
     * Esegue una visita in ampiezza (Breadth-First Search) dell'albero.
     * 
     * @return lista dei valori dei nodi nell'ordine di visita
     */
    public List<T> breadthFirstTraversal() {
        List<T> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.add(current.getData());
            
            for (Node child : current.getChildren()) {
                queue.add(child);
            }
        }
        
        return result;
    }
    
    /**
     * Cerca un valore nell'albero.
     * 
     * @param value il valore da cercare
     * @return il nodo contenente il valore o null se non trovato
     */
    public Node find(T value) {
        return findHelper(root, value);
    }
    
    /**
     * Funzione di supporto per la ricerca di un valore.
     * 
     * @param node il nodo corrente
     * @param value il valore da cercare
     * @return il nodo contenente il valore o null se non trovato
     */
    private Node findHelper(Node node, T value) {
        if (node == null) {
            return null;
        }
        
        if (node.getData().equals(value)) {
            return node;
        }
        
        for (Node child : node.getChildren()) {
            Node result = findHelper(child, value);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Calcola l'altezza dell'albero (la lunghezza del percorso più lungo dalla radice a una foglia).
     * 
     * @return l'altezza dell'albero o -1 se l'albero è vuoto
     */
    public int height() {
        return heightHelper(root);
    }
    
    /**
     * Funzione di supporto per calcolare l'altezza.
     * 
     * @param node il nodo corrente
     * @return l'altezza del sottoalbero radicato in node
     */
    private int heightHelper(Node node) {
        if (node == null) {
            return -1;
        }
        
        int maxChildHeight = -1;
        for (Node child : node.getChildren()) {
            int childHeight = heightHelper(child);
            maxChildHeight = Math.max(maxChildHeight, childHeight);
        }
        
        return maxChildHeight + 1;
    }
    
    /**
     * Verifica se due alberi sono uguali in struttura e valori.
     * 
     * @param other l'altro albero da confrontare
     * @return true se gli alberi sono uguali, false altrimenti
     */
    public boolean equals(NaryTree<T> other) {
        return equalsHelper(this.root, other.root);
    }
    
    /**
     * Funzione di supporto per verificare l'uguaglianza.
     * 
     * @param node1 il nodo del primo albero
     * @param node2 il nodo del secondo albero
     * @return true se i sottoalberi sono uguali, false altrimenti
     */
    private boolean equalsHelper(Node node1, Node node2) {
        if (node1 == null && node2 == null) {
            return true;
        }
        
        if (node1 == null || node2 == null) {
            return false;
        }
        
        if (!node1.getData().equals(node2.getData())) {
            return false;
        }
        
        if (node1.getChildCount() != node2.getChildCount()) {
            return false;
        }
        
        List<Node> children1 = node1.getChildren();
        List<Node> children2 = node2.getChildren();
        
        for (int i = 0; i < children1.size(); i++) {
            if (!equalsHelper(children1.get(i), children2.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Genera una rappresentazione testuale dell'albero.
     * 
     * @return stringa che rappresenta l'albero
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(root, sb, 0);
        return sb.toString();
    }
    
    /**
     * Funzione di supporto per la rappresentazione testuale.
     * 
     * @param node il nodo corrente
     * @param sb il StringBuilder per costruire la stringa
     * @param depth la profondità corrente
     */
    private void toStringHelper(Node node, StringBuilder sb, int depth) {
        if (node == null) {
            return;
        }
        
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        
        sb.append(node.toString()).append("\n");
        
        for (Node child : node.getChildren()) {
            toStringHelper(child, sb, depth + 1);
        }
    }
    
    /**
     * Metodo main per testare l'implementazione dell'albero n-ario.
     */
    public static void main(String[] args) {
        // Creiamo un albero con radice "A"
        NaryTree<String> tree = new NaryTree<>("A");
        
        // Otteniamo il nodo radice
        NaryTree<String>.Node root = tree.getRoot();
        
        // Aggiungiamo figli a root
        NaryTree<String>.Node nodeB = tree.addChild(root, "B");
        NaryTree<String>.Node nodeC = tree.addChild(root, "C");
        NaryTree<String>.Node nodeD = tree.addChild(root, "D");
        
        // Aggiungiamo figli a nodeB
        NaryTree<String>.Node nodeE = tree.addChild(nodeB, "E");
        NaryTree<String>.Node nodeF = tree.addChild(nodeB, "F");
        
        // Aggiungiamo figli a nodeC
        NaryTree<String>.Node nodeG = tree.addChild(nodeC, "G");
        
        // Aggiungiamo figli a nodeD
        NaryTree<String>.Node nodeH = tree.addChild(nodeD, "H");
        NaryTree<String>.Node nodeI = tree.addChild(nodeD, "I");
        NaryTree<String>.Node nodeJ = tree.addChild(nodeD, "J");
        
        // Aggiungiamo figli a nodeF
        NaryTree<String>.Node nodeK = tree.addChild(nodeF, "K");
        
        // Visualizziamo l'albero
        System.out.println("Struttura dell'albero:");
        System.out.println(tree);
        
        // Mostriamo le informazioni sull'albero
        System.out.println("Numero di nodi: " + tree.size());
        System.out.println("Altezza dell'albero: " + tree.height());
        
        // Eseguiamo una visita in profondità
        System.out.println("Visita in profondità (DFS): " + tree.depthFirstTraversal());
        
        // Eseguiamo una visita in ampiezza
        System.out.println("Visita in ampiezza (BFS): " + tree.breadthFirstTraversal());
        
        // Cerchiamo alcuni nodi
        System.out.println("Nodo con valore 'F': " + tree.find("F"));
        System.out.println("Nodo con valore 'Z': " + tree.find("Z"));
        
        // Rimuoviamo un sottoalbero
        System.out.println("Rimozione del sottoalbero con radice 'D'...");
        tree.removeNode(nodeD);
        
        // Visualizziamo l'albero dopo la rimozione
        System.out.println("Struttura dell'albero dopo la rimozione:");
        System.out.println(tree);
        System.out.println("Numero di nodi dopo la rimozione: " + tree.size());
    }
}