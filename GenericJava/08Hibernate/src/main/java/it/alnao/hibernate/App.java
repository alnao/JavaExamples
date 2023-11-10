package it.alnao.hibernate;
 
import org.hibernate.Query;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class App{
	
    public static void main( String[] args )    {
        System.out.println( "Hello World!" );
        Model m=new Model();
        m.setId(1);
        m.setNome("Alberto");
        m.setCognome("Nao");
        Session session = HibernateUtil.getSessionFactory().openSession();
    	try{
    	      //tx = session.beginTransaction();
    	      session.save(m); 
    	      session.flush();
    	      System.out.println("Saved Successfully.");
    	      Query q = session.createQuery("from Model");
    	      List<Model> resultList = q.list();
    	      System.out.println("num:" + resultList.size());
    	      for (Model next : resultList) {
    	        	System.out.println("- " + next);
    	      }
    	      session.delete(resultList.get(0));
    	  }catch (Exception e) {
    	     e.printStackTrace(); 
    	  }finally {
    	     session.close(); 
    	  }        
    }
}
