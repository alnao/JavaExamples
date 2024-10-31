package it.alnao; 

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * Comandi per compilazione ed esecuzione
 * mvn clean dependency:copy-dependencies package
 * java -jar target\16openCSV-1.0-SNAPSHOT.jar
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

       // Carica le properties dalla cartella resources
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

        // Configura il lettore CSV per usare il punto e virgola come separatore
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(inputPath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            List<String[]> allRows = new ArrayList<>();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                allRows.add(nextLine);
            }

            List<String[]> filteredRows = filterCSV(allRows);

            // Configura lo scrittore CSV per usare il punto e virgola come separatore e disabilitare i doppi apici
            try (CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(outputPath))
                    .withSeparator(';')
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build()) {
                writeCSV(writer, filteredRows);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> filterCSV(List<String[]> allRows) {
        List<String[]> filteredRows = new ArrayList<>();
        if (!allRows.isEmpty()) {
            // Aggiungi l'intestazione
            filteredRows.add(allRows.get(0));
            for (String[] row : allRows.subList(1, allRows.size())) {
                int eta = Integer.parseInt(row[2]);
                if (eta > 40) {
                    filteredRows.add(row);
                }
            }
        }
        return filteredRows;
    }

    public static void writeCSV(CSVWriter writer, List<String[]> filteredRows) throws IOException {
        writer.writeAll(filteredRows);
    }
}
