package it.alnao.examples;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println( dividi(new Double(42) , new Double(16) ));
    }
    public static Double dividi(Double dividendo, Double divisore) throws NullPointerException,ArithmeticException{
        if ((dividendo==null) || (divisore==null)){
            throw new NullPointerException("Divisore o dividendo nullo");
        }
        if (divisore==0.0){
            throw new ArithmeticException("Impossibile dividere per zero");
        }
        return new Double (dividendo/divisore);
    }
}
