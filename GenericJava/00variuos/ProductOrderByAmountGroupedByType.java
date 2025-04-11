//in java, implementa le classi Category con nome e order (int), Product con nome, category, quantity (int) e price (double). 
//implementa metodo getTotalAmountByCategory che prende una lista di Product in input e ritorna una struttura dati Map che indica, per ogni categoria, la somma degli amount (quantità * prezzo).
// implementa metodo getProductOrderByAmountGroupedByType che prende la lista di product in input e ritorna struttura Map che contiene categoria di prodotto nella collezione con l'elenco dei prodotti di quel tipo ordinati per amount
import java.util.*;
public class ProductOrderByAmountGroupedByType {
    public static Map<Category, List<Product>> getProductOrderByAmountGroupedByType(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return new HashMap<>();
        }
        Map<Category, List<Product>> resultMap = new HashMap<>();
        for (Product product : products) {// Raggruppiamo i prodotti per categoria
            Category category = product.getCategory();
            if (resultMap.containsKey(category)) {// Se la categoria è già nella mappa, aggiungiamo il prodotto alla lista esistente
                List<Product> productList = resultMap.get(category);
                productList.add(product);
            } else {// Altrimenti, creiamo una nuova lista con il prodotto
                List<Product> productList = new ArrayList<>();
                productList.add(product);
                resultMap.put(category, productList);
            }
        }
        // Ordiniamo ogni lista di prodotti per amount
        for (Map.Entry<Category, List<Product>> entry : resultMap.entrySet()) {
            List<Product> productList = entry.getValue();
            // Ordinamento della lista per amount usando Collections.sort
            Collections.sort(productList, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    return Double.compare(p1.getAmount(), p2.getAmount());
                }
            });
        }
        return resultMap;
    }
    public static Map<Category, Double> getTotalAmountByCategory(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return new HashMap<>();
        }
        Map<Category, Double> resultMap = new HashMap<>();
        for (Product product : products) {
            Category category = product.getCategory();
            double amount = product.getAmount();
            // Se la categoria è già nella mappa, aggiungiamo l'amount al valore esistente
            if (resultMap.containsKey(category)) {
                double currentAmount = resultMap.get(category);
                resultMap.put(category, currentAmount + amount);
            } else {
                // Altrimenti, inseriamo la nuova categoria con il relativo amount
                resultMap.put(category, amount);
            }
        }
        return resultMap;
    }


    static class Category {
        private String name;
        private int order;

        public Category(String name, int order) {
            this.name = name;
            this.order = order;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return "Category{name='" + name + "', order=" + order + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Category category = (Category) o;
            return order == category.order && Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, order);
        }
    }

    static class Product {
        private String name;
        private Category category;
        private int quantity;
        private double price;

        public Product(String name, Category category, int quantity, double price) {
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters e setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getAmount() {
            return quantity * price;
        }
        @Override
        public int compareTo(Product other) {
            return Double.compare(this.getAmount(), other.getAmount());
        }

        @Override
        public String toString() {
            return "Product{name='" + name + "', category=" + category + 
                ", quantity=" + quantity + ", price=" + price + ", amount=" + getAmount() + '}';
        }
    }

    public static void main(String[] args) {
        // Creazione delle categorie
        ProductOrderByAmountGroupedByType.Category elettronica = 
            new ProductOrderByAmountGroupedByType.Category("Elettronica", 1);
        ProductOrderByAmountGroupedByType.Category alimentari = 
            new ProductOrderByAmountGroupedByType.Category("Alimentari", 2);
        ProductOrderByAmountGroupedByType.Category abbigliamento = 
            new ProductOrderByAmountGroupedByType.Category("Abbigliamento", 3);
        
        // Creazione di alcuni prodotti
        List<ProductOrderByAmountGroupedByType.Product> products = new ArrayList<>();
        
        // Prodotti di elettronica
        products.add(new ProductOrderByAmountGroupedByType.Product("Smartphone", elettronica, 2, 500.0));
        products.add(new ProductOrderByAmountGroupedByType.Product("Laptop", elettronica, 1, 1200.0));
        products.add(new ProductOrderByAmountGroupedByType.Product("Cuffie", elettronica, 3, 80.0));
        
        // Prodotti alimentari
        products.add(new ProductOrderByAmountGroupedByType.Product("Pasta", alimentari, 5, 1.2));
        products.add(new ProductOrderByAmountGroupedByType.Product("Olio", alimentari, 2, 6.5));
        products.add(new ProductOrderByAmountGroupedByType.Product("Formaggio", alimentari, 1, 8.0));
        
        // Prodotti di abbigliamento
        products.add(new ProductOrderByAmountGroupedByType.Product("Maglietta", abbigliamento, 3, 15.0));
        products.add(new ProductOrderByAmountGroupedByType.Product("Jeans", abbigliamento, 2, 40.0));
        products.add(new ProductOrderByAmountGroupedByType.Product("Scarpe", abbigliamento, 1, 80.0));
        
        System.out.println("Lista di prodotti:");
        for (ProductOrderByAmountGroupedByType.Product product : products) {
            System.out.println(product);
        }
        
        System.out.println("\n---------- Test getTotalAmountByCategory ----------");
        Map<ProductOrderByAmountGroupedByType.Category, Double> totalAmountByCategory = 
            ProductOrderByAmountGroupedByType.getTotalAmountByCategory(products);
        
        for (Map.Entry<ProductOrderByAmountGroupedByType.Category, Double> entry : totalAmountByCategory.entrySet()) {
            System.out.printf("Categoria: %s, Totale: %.2f€\n", entry.getKey().getName(), entry.getValue());
        }
        
        System.out.println("\n---------- Test getProductOrderByAmountGroupedByType ----------");
        Map<ProductOrderByAmountGroupedByType.Category, List<ProductOrderByAmountGroupedByType.Product>> productsByCategory = 
            ProductOrderByAmountGroupedByType.getProductOrderByAmountGroupedByType(products);
        
        for (Map.Entry<ProductOrderByAmountGroupedByType.Category, List<ProductOrderByAmountGroupedByType.Product>> entry : productsByCategory.entrySet()) {
            System.out.println("\nCategoria: " + entry.getKey().getName());
            System.out.println("Prodotti ordinati per amount (prezzo * quantità):");
            
            for (ProductOrderByAmountGroupedByType.Product product : entry.getValue()) {
                System.out.printf("  - %s: %.2f€ (quantità: %d, prezzo: %.2f€)\n", 
                    product.getName(), product.getAmount(), product.getQuantity(), product.getPrice());
            }
        }
        
        // Test con lista vuota
        System.out.println("\n---------- Test con lista vuota ----------");
        Map<ProductOrderByAmountGroupedByType.Category, Double> emptyResult1 = 
            ProductOrderByAmountGroupedByType.getTotalAmountByCategory(new ArrayList<>());
        System.out.println("getTotalAmountByCategory con lista vuota: " + emptyResult1);
        
        Map<ProductOrderByAmountGroupedByType.Category, List<ProductOrderByAmountGroupedByType.Product>> emptyResult2 = 
            ProductOrderByAmountGroupedByType.getProductOrderByAmountGroupedByType(new ArrayList<>());
        System.out.println("getProductOrderByAmountGroupedByType con lista vuota: " + emptyResult2);
    }
}
