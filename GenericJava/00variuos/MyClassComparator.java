//in java, implementa le classi Category con nome e order (int), Product con nome, category, quantity (int) e price (double). 
//implementa metodo getTotalAmountByCategory che prende una lista di Product in input e ritorna una struttura dati Map che indica, 
//per ogni categoria, la somma degli amount (quantità * prezzo).
// implementa metodo getProductOrderByAmountGroupedByType che prende la lista di product in input e ritorna struttura Map che contiene categoria di prodotto nella collezione con l'elenco dei prodotti di quel tipo ordinati per amount

import java.util.*;
public class MyClass {
    static public class ProductComparator implements Comparator<Product>{
        public int compare(Product p1 , Product p2){
            return Double.compare(p1.getAmount(),p2.getAmount());
        }
      }
  public static Map<Category,List<Product>> getProductOrderByAmountGroupedByType(Product productList[]){
      Map<Category,List<Product>> map=new HashMap<Category,List<Product>>();
      for ( Product product : productList){
         if (! map.containsKey(product.getCategory() )  ){
            List<Product> l=new ArrayList<Product>();
            l.add(product);
            map.put( product.getCategory() , l );
         }else{
            List<Product> l=map.get(product.getCategory() );
            l.add(product);
            //map.put( product.getCategory() , l );
         }
      }
      for ( Category category : map.keySet() ){
          List<Product> l=map.get(category );
          Collections.sort(l , new ProductComparator() );
      }
      return map;
  }
  
  public static  Map<Category,Double> getTotalAmountByCategory(Product productList[]){
     Map<Category,Double> map=new HashMap<Category,Double>();
     for ( Product e : productList){
         if ( map.containsKey(e.getCategory() )  ){
             double prevAmount=(map.get(e.getCategory()));
             map.put(e.getCategory(),e.getAmount() + prevAmount);
         }else{
             map.put(e.getCategory(),e.getAmount());
         }
     }
     return map;
  }

    
  static public class Product{
      private String nome;
      private Category category;
      private int quantity;
      private double price;
      public Product(String nome, Category category, int quantity, double price){
          this.nome=nome;
          this.category=category;
          this.quantity=quantity;
          this.price=price;
      }
      public String getNome(){return nome;}
      public Category getCategory(){return category;}
      public int getQuantity(){return quantity;}
      public double getPrice(){return price;}
      public double getAmount(){return quantity*price;}

  }
  static public class Category{
      private String nome;
      public int order;
      public Category(String nome,int order){
          this.nome=nome;
          this.order=order;
      }
      public String getNome(){ return nome;}
      public int getOrder(){return order;}
  }
  
 

  public static void main(String args[]) {
    Product productList[] = new Product[5];
    Category cat1=new Category("C1",1);
    Category cat2=new Category("C2",2);
    productList[0]=new Product("P1",cat1,2,1.5);
    productList[1]=new Product("P2",cat2,42,0.5);
    productList[2]=new Product("P3",cat1,2,30);
    productList[3]=new Product("P4",cat1,1,5);
    productList[4]=new Product("P5",cat2,10,0.1);
    
    Map<Category,Double> result=getTotalAmountByCategory(productList);
    
    for (Category c : result.keySet() ){
      System.out.println( c.getNome() + " " + result.get(c) ); 
    }
    
    Map<Category,List<Product>> listOrdered=getProductOrderByAmountGroupedByType(productList);
    for ( Category c : listOrdered.keySet() ){
      System.out.println( "\nCategoria:" + c.getNome());
      for (Product p : listOrdered.get(c) ){
          System.out.println("\n  "+p.getNome() + " "  + p.getAmount() );
      }
    }
}
  
  
}