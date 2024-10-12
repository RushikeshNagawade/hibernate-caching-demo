package org.ahad.hibernate;

import org.ahad.hibernate.config.SessionProvider;
import org.ahad.hibernate.entity.Employee;
import org.hibernate.Session;

public class FirstLevelCache {
    public static void main(String[] args) {

        Session session = SessionProvider.openSession();

        try {
            session.beginTransaction();
            Employee employee = new Employee("Bill", "Gates", "elon.mush@example.com");
            session.save(employee);
            session.getTransaction().commit();
            System.out.println("Employee saved successfully!");
        } finally {
            SessionProvider.closeSession(session);
        }

        SessionProvider.closeSessionFactory();

    }
}
