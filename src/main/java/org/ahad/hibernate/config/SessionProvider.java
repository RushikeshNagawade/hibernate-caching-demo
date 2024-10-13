package org.ahad.hibernate.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

public class SessionProvider {

    private static final SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

    public static Session openSession() {
        return sessionFactory.openSession();
    }

    public static void closeSession(Session... sessions) {
        for (Session session : sessions) {
            session.close();
        }
    }

    public static void closeSessionFactory() {
        sessionFactory.close();
    }

    public static Statistics getSessionFactoryStatistics() {
        return sessionFactory.getStatistics();
    }
}
