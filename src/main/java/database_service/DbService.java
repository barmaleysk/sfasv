package database_service;

import entitys.User;

import javax.persistence.*;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class DbService {
    EntityManager em;
    public DbService() {
        this.em = Persistence.createEntityManagerFactory("MySql").createEntityManager();
    }


    public void addRootUser(User user){
        TypedQuery<Integer> typedQuery = em.createNamedQuery("User.getMaxRightKey",Integer.class);
        Integer maxRightKey=typedQuery.getSingleResult();
        if (maxRightKey!=null) {
            user.setLevel(0);
            user.setLeftKey(maxRightKey + 1);
            user.setRightKey(maxRightKey + 2);
        }else {
            user.setLevel(0);
            user.setLeftKey(1);
            user.setRightKey(2);
        }
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(user);
        transaction.commit();
    }
    public User getUserFromDb(long userId){
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User usserFromDb = em.find(User.class,userId);
        transaction.commit();
        return usserFromDb;
    }

    public void addChildrenUser(long parentUserId,User childrenUser){
        User parentUser = getUserFromDb(parentUserId);
        int rightKey = parentUser.getRightKey();
        childrenUser.setLevel(parentUser.getLevel()+1);
        childrenUser.setLeftKey(rightKey);
        childrenUser.setRightKey(rightKey+1);

        Query query = em.createNamedQuery("User.calculateKeyStep1");
        query.setParameter("key",rightKey);
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        query.executeUpdate();
        transaction.commit();

        query = em.createNamedQuery("User.calculateKeyStep2");
        query.setParameter("key",rightKey);
        transaction.begin();
        query.executeUpdate();
        transaction.commit();

        transaction.begin();
        em.persist(childrenUser);
        transaction.commit();
    }
}
