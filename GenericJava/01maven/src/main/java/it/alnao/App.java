package it.alnao;


//import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    
    public static void main( String[] args )    {
	BasicConfigurator.configure();
        System.out.println( "Hello World!" );
        logger.debug("We've just greeted the user!");
        logger.info("We've just greeted the user!");
        logger.fatal("We've just greeted the user!");
    }
}
