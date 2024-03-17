package it.alnao.examples;

import static org.junit.jupiter.api.Assertions.fail;

//import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
//import org.junit.Test;
import org.junit.jupiter.api.Test;


public class AppTest {

    @Test
    public void shouldAnswerWithTrue() {
        Assertions.assertTrue( true );
    }

    @Test
    @DisplayName("Dividi 42")
    public void dividi42() throws Exception{
        //1) Arrange: la definizione di tutti i dati di partenza 
        Double dividendo=new Double(42.0);
        Double divisore=new Double(16.0);
        Double resultAtteso=new Double(2.625);
        //2) Act: la chiamata alla operazione da testare (di solito è una sola operazione ma può essere anche multipla)
        Double result=App.dividi(dividendo, divisore);
        //3) Assert: la validazione del risultato confrontando la risposta del passo 2 con quanto definito nel punto 1        
        Assertions.assertEquals(resultAtteso,result);
    }

    @Test
    @DisplayName("Dividi per zero")
    public void dividiPerZero(){
        //1) Arrange: la definizione di tutti i dati di partenza 
        final Double dividendo=new Double(5.0);
        final Double divisore=new Double(0);
        //3) Assert: la validazione del risultato confrontando la risposta del passo 2 con quanto definito nel punto 1        
        Assertions.assertThrows(ArithmeticException.class,
            ()->{ //2) Act: la chiamata alla operazione da testare (di solito è una sola operazione ma può essere anche multipla)
                App.dividi(dividendo, divisore);
            }
        );
    }

    @Test
    @Disabled("Disabled test example")
    void disabledTest() {
        fail();
    }

    @BeforeAll
    static void setup() {
        //log.info("@BeforeAll - executes once before all test methods in this class");
    }
    
    @BeforeEach
    void init() {
        //log.info("@BeforeEach - executes before each test method in this class");
    }
}
