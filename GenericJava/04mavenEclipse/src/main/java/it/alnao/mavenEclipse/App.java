package it.alnao.mavenEclipse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class App {
	private static final Logger logger = LogManager.getLogger(App.class);
	
    public static void main( String[] args ){
    	BasicConfigurator.configure();
        System.out.println( "Hello World!" );
        logger.info("Hello world");
        //set a breakpoint here to debug project
        System.out.println( "END!" );
    }
}
