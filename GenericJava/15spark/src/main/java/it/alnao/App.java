package it.alnao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Comandi per compilazione ed esecuzione
 * mvn clean dependency:copy-dependencies package
 * java -jar target\Spark-1.0-SNAPSHOT.jar
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        // Carica le properties
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Spiacente, non riesco a trovare config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Ottieni i percorsi dal file di properties
        String inputPath = properties.getProperty("input.path");
        String outputPath = properties.getProperty("output.path");

        // Crea una sessione Spark
        SparkSession spark = SparkSession.builder()
                .appName("FilterCSV")
                .master("local[*]") 
                .getOrCreate();

        // Leggi il file CSV
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .option("delimiter",";")
                .csv(inputPath);

        // Filtra le persone con età > 40
        Dataset<Row> filteredDF = df.filter(df.col("age").gt(40));

        // Salva il risultato in un nuovo file CSV
        filteredDF.write()
                .option("header", "true")
                .option("delimiter",";")
                .csv(outputPath);

        // Ferma la sessione Spark
        spark.stop();
    }
}
