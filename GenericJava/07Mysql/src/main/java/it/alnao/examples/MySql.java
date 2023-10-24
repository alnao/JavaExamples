package it.alnao.examples;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MySql {
    private static final Logger logger = LogManager.getLogger(MySql.class);
    private final static String URL = "jdbc:mysql://localhost:3306/dbname";
    private final static String USERNAME = "user";
    private final static String PASSWORD = "password";
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    
    public static void main( String[] args ) throws SQLException, ClassNotFoundException    {
    	BasicConfigurator.configure();
        //System.out.println( "Hello World!" );
        logger.debug("We've just greeted the user!");
        //logger.info("We've just greeted the user!");
        //logger.fatal("We've just greeted the user!");
        
        String sqlCommand="select nome, cognome from tabella where cognome is not null order by cognome";
        Class.forName(DRIVER);
        Connection con = DriverManager.getConnection (URL,USERNAME,PASSWORD);
        PreparedStatement cmd = con.prepareStatement(sqlCommand);
        ResultSet res = cmd.executeQuery();
        ArrayList<String> nomi=new ArrayList<String>();
        if (res!=null){
        	while(res.next()) {
        		logger.debug("name:" + res.getString("nome"));
        		nomi.add( res.getString("nome") );
        	}
        }
        cmd.close();
        con.close();
    }
}
