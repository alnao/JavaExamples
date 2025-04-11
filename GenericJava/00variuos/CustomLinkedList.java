// Ricreare ex-novo a livello logico una LinkedList (rami e foglie)
// sottoclasse Nodo con Data<T> e NEXT node
// attributi head,tail, size
// metodi isEmpty, size, add, remove

public class CustomLinkedList<T> {

    // Attributi della LinkedList
    private Node head;    // Testa della lista
    private Node tail;    // Coda della lista
    private int size;     // Dimensione della lista

    public int add(Data data){
        Node newNode = new Node(data);
        if ( isEmpty() ){
            this.head = newNode;
            this.tail = newNode;
        }else{
            this.tail.next=newNode;
            this.tail=newNode;
        }
        return this.size++;
    }
    public int remove(int index){
        if (index<0 || index>=size){
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (size == 1){
            this.head=this.head.next;
            this.size--; 
        }
        Node toRemove = this.head;
        Node previous = null;
        for (int i=0; i<index; i++){ //cerco i-esimo
            previous = toRemove;
            toRemove = current.next;
        }
        previous.next = toRemove.next;
        if (toRemove==this.tail){
            this.tail = previous;
        }
        return this.size--;
    }

    // Definizione del nodo interno (ramo)
    private class Node {
        T data;       // Il dato contenuto nel nodo (foglia)
        Node next;    // Riferimento al nodo successivo

        // Costruttore del nodo
        public Node(T data) {
            this.data = data;
            this.next = null;
        }
        public getData(){
            return data;
        }
        public setData(T data){
            this.data = data;
        }
        public getNext(){
            return next;
        }
        public setNext(Next next){
            this.next = next;
        }
    }


    // Costruttore della LinkedList
    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    // isEmpty, size, add, remove
    public boolean isEmpty(){
        return this.size==0;
    }
    public int size(){
        return this.size;
    }

    

}

