package org.ahad.hibernate;

import org.ahad.hibernate.config.SessionProvider;
import org.ahad.hibernate.entity.Employee;
import org.ahad.hibernate.entity.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.SelectionQuery;
import org.hibernate.stat.Statistics;

public class SecondLevelCache {

    public static void main(String[] args) {
        saveData();
        System.out.println("Save operation completed");

        enableSessionStatistics();
        retrieveEmployee();
        printCacheStatistics();
        retrievePerson();
        printCacheStatistics();
        System.out.println("Retrieve operation completed");
        System.out.println();

        enableSessionStatistics();
        retrieveEmployeeWithQuery();
        printQueryCacheStatistics();
        retrievePersonWithQuery();
        printQueryCacheStatistics();
        System.out.println("Retrieve with query operation completed");
        System.out.println();



        SessionProvider.closeSessionFactory();
    }

    private static void saveData() {
        Session session = SessionProvider.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Employee employee = new Employee("Bill", "Gates", "bill.gates@example.com");
            session.persist(employee);
            Person person = new Person( "Ahad Ali", 23);
            session.persist(person);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            SessionProvider.closeSession(session);
        }
    }

    private static void retrieveEmployee() {
        Session session1 = SessionProvider.openSession();
        Session session2 = SessionProvider.openSession();
        Employee employee1 = session1.get(Employee.class, 1L);  // Fetch from DB
        System.out.println("First Session Fetch: " + employee1);
        Employee employee2 = session2.get(Employee.class, 1L);  // Won't hit cache
        System.out.println("Second Session Fetch (from cache): " + employee2);
        SessionProvider.closeSession(session1, session2);
    }

    private static void retrievePerson() {
        Session session1 = SessionProvider.openSession();
        Session session2 = SessionProvider.openSession();
        Person person1 = session1.get(Person.class, 1L);  // Fetch from DB
        System.out.println("First Session Fetch: " + person1);
        Person person2 = session2.get(Person.class, 1L);  // Should hit cache
        System.out.println("Second Session Fetch (from cache): " + person2);
        SessionProvider.closeSession(session1, session2);

    }

    private static void retrieveEmployeeWithQuery() {
        Session session1 = SessionProvider.openSession();
        Session session2 = SessionProvider.openSession();
        SelectionQuery<Employee> query1 = session1.createSelectionQuery("from Employee where id = :id", Employee.class)
                .setParameter("id", 1L)
                .setCacheable(true)
                .setCacheRegion("queryCache");

        Employee employee1 = query1.getSingleResult();
        System.out.println("First Session Fetch: " + employee1);

        SelectionQuery<Employee> query2 = session2.createSelectionQuery("from Employee where id = :id", Employee.class)
                .setParameter("id", 1L)
                .setCacheable(true)
                .setCacheRegion("queryCache");
        Employee employee2 = query2.getSingleResult();  // Should hit cache
        System.out.println("Second Session Fetch (from cache): " + employee2);
        SessionProvider.closeSession(session1, session2);
    }

    private static void retrievePersonWithQuery() {
        Session session1 = SessionProvider.openSession();
        Session session2 = SessionProvider.openSession();
        SelectionQuery<Person> query1 = session1.createSelectionQuery("from Person where id = :id", Person.class)
                .setParameter("id", 1L)
                .setCacheable(true)
                .setCacheRegion("queryCache");

        Person person1 = query1.getSingleResult();  // Fetch from DB
        System.out.println("First Session Fetch: " + person1);

        SelectionQuery<Person> query2 = session2.createSelectionQuery("from Person where id = :id", Person.class)
                .setParameter("id", 1L)
                .setCacheable(true)
                .setCacheRegion("queryCache");

        Person person2 = query2.getSingleResult();  // Should hit cache
        System.out.println("Second Session Fetch (from cache): " + person2);
        SessionProvider.closeSession(session1, session2);
    }

    private static void enableSessionStatistics() {
        Statistics stats = SessionProvider.getSessionFactoryStatistics();
        stats.setStatisticsEnabled(true);
    }

    private static void printCacheStatistics() {
        Statistics stats = SessionProvider.getSessionFactoryStatistics();
        System.out.println("Second level cache hit count: " + stats.getSecondLevelCacheHitCount());
        System.out.println("Second level cache miss count: " + stats.getSecondLevelCacheMissCount());
    }
    private static void printQueryCacheStatistics() {
        Statistics stats = SessionProvider.getSessionFactoryStatistics();
        System.out.println("Query cache hit count: " + stats.getQueryCacheHitCount());
        System.out.println("Query cache miss count: " + stats.getQueryCacheMissCount());
    }

}
