import java.util.*;

public class ShareAreaType{
    // Restituisce la somma delle aree per tipo, ordinata per tipo
    public static Map<String, Double> getAreaSumByType(List<Shape> shapes) {
        // Utilizzo di una mappa per accumulare le somme delle aree per tipo
        Map<String, Double> areaSumByType = new HashMap<>();
        // Calcolo della somma delle aree per ciascun tipo
        for (Shape shape : shapes) {
            String type = shape.getType();
            double area = shape.getArea();
            areaSumByType.put(type, areaSumByType.getOrDefault(type, 0.0) + area);
        }
        // Conversione in TreeMap per l'ordinamento per chiave (tipo)
        return new TreeMap<>(areaSumByType);
    }
    // Restituisce le shape raggruppate per tipo e ordinate per area
    public static Map<String, List<Shape>> getShapesOrderByAreaGroupedByType(List<Shape> shapes) {
        // Raggruppa per tipo usando TreeMap per l'ordinamento automatico delle chiavi
        Map<String, List<Shape>> result = new TreeMap<>();

        // Raggruppamento per tipo
        for (Shape shape : shapes) {
            String type = shape.getType();
            if (!result.containsKey(type)) {
                result.put(type, new ArrayList<>());
            }
            result.get(type).add(shape);
        }

        // Ordinamento di ciascun gruppo per area
        for (List<Shape> shapeList : result.values()) {
            Collections.sort(shapeList, Comparator.comparingDouble(Shape::getArea));
        }

        return result;
    }



    // Classe astratta Shape
    static abstract class Shape {
        public abstract double getArea();
        public abstract String getType();
    }
    static class Rectangle extends Shape {
        private double base;
        private double height;
        public Rectangle(double base, double height) {
            this.base = base;
            this.height = height;
        }
        @Override
        public double getArea() {
            return base * height;
        }
        @Override
        public String getType() {
            return "Rectangle";
        }
    }
    static class Circle extends Shape {
        private double radius;
        public Circle(double radius) {
            this.radius = radius;
        }
        @Override
        public double getArea() {
            return Math.PI * radius * radius;
        }
        @Override
        public String getType() {
            return "Circle";
        }
        public double getRadius() {
            return radius;
        }
    }

    // Metodo main per testare l'implementazione
    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();

        // Aggiunta di alcune figure
        shapes.add(new Rectangle(3, 4));      // Area = 12
        shapes.add(new Circle(2));            // Area ≈ 12.57
        shapes.add(new Rectangle(5, 6));      // Area = 30
        shapes.add(new Circle(3));            // Area ≈ 28.27
        shapes.add(new Rectangle(2, 2));      // Area = 4

        // Test del primo metodo
        System.out.println("Area sum by type (sorted by type):");
        Map<String, Double> areaSumByType = getAreaSumByType(shapes);
        for (Map.Entry<String, Double> entry : areaSumByType.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Test del secondo metodo
        System.out.println("\nShapes grouped by type and ordered by area:");
        Map<String, List<Shape>> shapesGrouped = getShapesOrderByAreaGroupedByType(shapes);

        for (Map.Entry<String, List<Shape>> entry : shapesGrouped.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (Shape shape : entry.getValue()) {
                System.out.println("  " + shape);
            }
        }
    }

}