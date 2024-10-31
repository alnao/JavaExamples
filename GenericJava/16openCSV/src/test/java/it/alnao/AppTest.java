package it.alnao;

import org.junit.jupiter.api.Test;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AppTest {

    private Properties properties;

    @BeforeEach
    public void setUp() {
        properties = new Properties();
    }

    @Test
    public void testLoadProperties() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
        assertNotNull(inputStream, "Spiacente, non riesco a trovare config.properties");
        properties.load(inputStream);
        assertEquals("C:/temp/input2.csv", properties.getProperty("input.path"));
        assertEquals("C:/temp/output2.csv", properties.getProperty("output.path"));
    }

    @Test
    public void testFilterCSV() {
        // Mock data
        List<String[]> allRows = new ArrayList<>();
        allRows.add(new String[]{"nome", "cognome", "eta"});
        allRows.add(new String[]{"Mario", "Rossi", "45"});
        allRows.add(new String[]{"Luigi", "Verdi", "30"});
        allRows.add(new String[]{"Anna", "Bianchi", "50"});

        // Call the method to test
        List<String[]> filteredRows = App.filterCSV(allRows);

        // Verify the results
        assertEquals(3, filteredRows.size());
        assertArrayEquals(new String[]{"nome", "cognome", "eta"}, filteredRows.get(0));
        assertArrayEquals(new String[]{"Mario", "Rossi", "45"}, filteredRows.get(1));
        assertArrayEquals(new String[]{"Anna", "Bianchi", "50"}, filteredRows.get(2));
    }

    @Test
    public void testWriteCSV() throws IOException {
        // Mock CSVWriter
        CSVWriter writer = mock(CSVWriter.class);

        // Mock data
        List<String[]> filteredRows = new ArrayList<>();
        filteredRows.add(new String[]{"nome", "cognome", "eta"});
        filteredRows.add(new String[]{"Mario", "Rossi", "45"});
        filteredRows.add(new String[]{"Anna", "Bianchi", "50"});

        // Call the method to test
        App.writeCSV(writer, filteredRows);

        // Verify writer interactions
        verify(writer, times(1)).writeAll(filteredRows);
    }
/*
    private Properties properties;
    private App app;

    @BeforeEach
    public void setUp() {
        properties = new Properties();
        app = new App();
    }


    @Test
    public void testLoadProperties() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
        assertNotNull(inputStream, "Spiacente, non riesco a trovare config.properties");
        properties.load(inputStream);
        assertEquals("C:/temp/input2.csv", properties.getProperty("input.path"));
        assertEquals("C:/temp/output2.csv", properties.getProperty("output.path"));
    }
    @Test
    public void testFilterCSV() throws Exception {
        // Mock data
        List<String[]> allRows = new ArrayList<>();
        allRows.add(new String[]{"nome", "cognome", "eta"});
        allRows.add(new String[]{"Mario", "Rossi", "45"});
        allRows.add(new String[]{"Luigi", "Verdi", "30"});
        allRows.add(new String[]{"Anna", "Bianchi", "50"});

        // Call the method to test
        List<String[]> filteredRows = app.filterCSV(allRows);

        // Verify the results
        assertEquals(3, filteredRows.size());
        assertArrayEquals(new String[]{"nome", "cognome", "eta"}, filteredRows.get(0));
        assertArrayEquals(new String[]{"Mario", "Rossi", "45"}, filteredRows.get(1));
        assertArrayEquals(new String[]{"Anna", "Bianchi", "50"}, filteredRows.get(2));
    }
    
    @Test
    public void testFilterCSVcomplete() throws Exception {
        // Mock CSVReader and CSVWriter
        CSVReader reader = mock(CSVReader.class);
        CSVWriter writer = mock(CSVWriter.class);

        // Mock data
        List<String[]> allRows = new ArrayList<>();
        allRows.add(new String[]{"nome", "cognome", "eta"});
        allRows.add(new String[]{"Mario", "Rossi", "45"});
        allRows.add(new String[]{"Luigi", "Verdi", "30"});
        allRows.add(new String[]{"Anna", "Bianchi", "50"});

        when(reader.readNext()).thenReturn(allRows.get(0), allRows.get(1), allRows.get(2), allRows.get(3), null);

        // Call the method to test
        List<String[]> filteredRows = app.filterCSV(allRows);

        // Verify the results
        assertEquals(3, filteredRows.size());
        assertArrayEquals(new String[]{"nome", "cognome", "eta"}, filteredRows.get(0));
        assertArrayEquals(new String[]{"Mario", "Rossi", "45"}, filteredRows.get(1));
        assertArrayEquals(new String[]{"Anna", "Bianchi", "50"}, filteredRows.get(2));

        // Verify writer interactions
        doNothing().when(writer).writeAll(filteredRows);
        app.writeCSV(writer, filteredRows);
        verify(writer, times(1)).writeAll(filteredRows);
    }  */
}