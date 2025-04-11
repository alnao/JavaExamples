//Scrivere una classe IntWrapper che abbia come unico attributo un intero e lo esponga all'esterno.
// Fornire un'implementazione dell'interfaccia SimpleStack o GenericStack Per implementare lo stack utilizzare un array di dimensione 1

//nota: lanciando così non funziona perchè NON so

// Implementazione dello stack con array di dimensione 1
class SingleElementStack<T> implements GenericStack<T> {
    // Interfaccia per uno stack generico
    interface GenericStack<T> {
        void push(T item);
        T pop();
        T peek();
        boolean isEmpty();
        boolean isFull();
    }
    public static void main(String[] args) {
        SingleElementStack<IntWrapper> stack = new SingleElementStack<>();
        stack.push(new IntWrapper(42));
        System.out.println(stack.peek()); // 42
        System.out.println(stack.pop()); // 42
        System.out.println(stack.isEmpty()); // true
    }
    
    private T[] stack;
    private int top;

    @SuppressWarnings("unchecked")
    public SingleElementStack() {
        stack = (T[]) new Object[1];
        top = -1; // Stack vuoto
    }
    @Override
    public void push(T item) {
        if (isFull()) {
            throw new IllegalStateException("Stack è pieno");
        }
        stack[++top] = item;
    }
    @Override
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack è vuoto");
        }
        return stack[top--];
    }
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack è vuoto");
        }
        return stack[top];
    }
    @Override
    public boolean isEmpty() {
        return top == -1;
    }
    @Override
    public boolean isFull() {
        return top == stack.length - 1;
    }



    // Classe IntWrapper che incapsula un intero
    static class IntWrapper {
        private int value;

        // Costruttore
        public IntWrapper(int value) {
            this.value = value;
        }

        // Getter
        public int getValue() {
            return value;
        }

        // Setter
        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }
}