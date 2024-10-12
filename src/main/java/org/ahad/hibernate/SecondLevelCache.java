package org.ahad.hibernate;

import org.ahad.hibernate.config.SessionProvider;
import org.ahad.hibernate.entity.Employee;
import org.ahad.hibernate.entity.Person;
import org.hibernate.Session;

public class SecondLevelCache {
    public static void main(String[] args) {
        saveEmployee();
        savePerson();
        System.out.println("Save operation completed\n\n\n");
        retrieveEmployee();
        retrievePerson();
        System.out.println("Save operation completed\n\n\n");

        SessionProvider.closeSessionFactory();
    }

    private static void saveEmployee() {
        Session session = SessionProvider.openSession();

        try {
            session.beginTransaction();
            Employee employee = new Employee("Bill", "Gates", "bill.gates@example.com");
            session.persist(employee);
            session.getTransaction().commit();
            System.out.println("Employee saved successfully with ID: " + employee.getId() + "\n");
        } finally {
            SessionProvider.closeSession(session);
        }
    }

    private static void savePerson() {
        Session session = SessionProvider.openSession();

        try {
            session.beginTransaction();
            Person person = new Person( "Ahad Ali", 23);
            session.persist(person);
            session.getTransaction().commit();
            System.out.println("Person saved successfully with ID: " + person.getId()+ "\n");
        } finally {
            SessionProvider.closeSession(session);
        }
    }

    private static void retrieveEmployee() {
        Session session1 = SessionProvider.openSession();
        Session session2 = SessionProvider.openSession();

        try {
            session1.beginTransaction();
            Employee employee1 = session1.get(Employee.class, 1L);  // Fetch from DB
            System.out.println("First Session Fetch: " + employee1);
            session1.getTransaction().commit();
            SessionProvider.closeSession(session1);

            session2.beginTransaction();
            Employee employee2 = session2.get(Employee.class, 1L);  // Should not hit cache
            System.out.println("Second Session Fetch (from cache): " + employee2);
            session2.getTransaction().commit();
        } finally {
            SessionProvider.closeSession(session2);
        }
    }

    private static void retrievePerson() {
        Session session1 = SessionProvider.openSession();
        Session session2 = SessionProvider.openSession();

        try {
            session1.beginTransaction();
            Person person1 = session1.get(Person.class, 1L);  // Fetch from DB
            System.out.println("First Session Fetch: " + person1);
            session1.getTransaction().commit();
            SessionProvider.closeSession(session1);

            session2.beginTransaction();
            Person person2 = session2.get(Person.class, 1L);  // Should hit cache
            System.out.println("Second Session Fetch (from cache): " + person2);
            session2.getTransaction().commit();
        } finally {
            SessionProvider.closeSession(session2);
        }
    }
}
