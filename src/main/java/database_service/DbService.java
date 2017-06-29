package database_service;

import entitys.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class DbService {
    EntityManager em;
    public DbService() {
        this.em = Persistence.createEntityManagerFactory("MySql").createEntityManager();
    }


    public User addUserInDb(User user){
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User userFormDb = em.merge(user);
        transaction.commit();
        return userFormDb;
    }
    public User getUserFromDb(long userId){
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User usserFromDb = em.find(User.class,userId);
        return usserFromDb;
    }
}
