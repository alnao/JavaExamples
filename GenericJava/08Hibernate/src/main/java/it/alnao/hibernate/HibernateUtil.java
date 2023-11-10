package it.alnao.hibernate;


import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
 
public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();
 
	private static SessionFactory buildSessionFactory() {
		SessionFactory sessionFactory = null;
		try {
        	Configuration configuration = new Configuration();
        	configuration.configure("hibernate.cfg.xml");
        	System.out.println("Hibernate Configuration loaded");
        	ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        	System.out.println("Hibernate serviceRegistry created");
        	sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            return sessionFactory;
			//return new  Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
			/*
			 * 
	        Configuration configuration = new Configuration();
            configuration.configure(new File(HibernateUtil.class.getClassLoader().getResource("hibernate.cfg.xml").toURI()));
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                    applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
            return sessionFactory ;
            */
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sessionFactory;
	}
 
	public static SessionFactory getSessionFactory() {
	  return sessionFactory;
	}
 
}
