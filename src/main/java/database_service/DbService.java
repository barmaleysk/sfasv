package database_service;

import entitys.Tasks;
import entitys.User;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class DbService {
    private static DbService dbService;
    private EntityManager em;
    private DbService() {
        this.em = Persistence.createEntityManagerFactory("eclipsMysql").createEntityManager();
    }

    public static DbService getInstance(){
        if (dbService==null)
            dbService=new DbService();
        return dbService;
    }

    public synchronized void updateUser(User user){
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(user);
        transaction.commit();
    }

    public synchronized void addRootUser(User user){
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

    public synchronized User getUserFromDb(long userId){
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User userFromDb = em.find(User.class,userId);
        transaction.commit();
        em.refresh(userFromDb);
        return userFromDb;
    }

    public synchronized boolean dbHasUser(long userId){
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User user = em.find(User.class,userId);
        transaction.commit();
        boolean check = false;
        if (user!=null)
            check=true;
        return check;
    }

    public synchronized void addChildrenUser (long parentUserId,User childrenUser) throws NoUserInDb {
        User parentUser = getUserFromDb(parentUserId);
        if (parentUser==null){
            throw new NoUserInDb();
        }
        int rightKey = parentUser.getRightKey();
        childrenUser.setLevel(parentUser.getLevel()+1);
        childrenUser.setLeftKey(rightKey);
        childrenUser.setRightKey(rightKey+1);

        Query query1 = em.createNamedQuery("User.calculateKeyStep1");
        query1.setParameter("key",rightKey);

        Query query2 = em.createNamedQuery("User.calculateKeyStep2");
        query2.setParameter("key",rightKey);
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        query1.executeUpdate();
        query2.executeUpdate();
        em.persist(childrenUser);

        transaction.commit();
    }

    public synchronized List<User> getChildrenUsers(int parentLevel,int parenLeftKey, int parentRightKey ){
        List<User> usersList = new ArrayList<>();
        TypedQuery<User> query = em.createNamedQuery("User.getAllChildren",User.class);
        query.setParameter("rk",parentRightKey);
        query.setParameter("lk",parenLeftKey);
        query.setParameter("l",parentLevel);

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        usersList=query.getResultList();
        transaction.commit();
        return usersList;
    }

    public synchronized LocalDate getEndOfSubscription(long userId) {
        User user = getUserFromDb(userId);
        return user.getEndDateOfSubscription().toLocalDate();
    }

    public synchronized  boolean wasSubscription(long userId){
        boolean was = true;
        LocalDate date = getEndOfSubscription(userId);
        if (date==null)
            was=false;
        return was;
    }


    public synchronized void addTask(long userID, Tasks task) {
        System.out.println("сохраняем tasks");
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,userID);
        System.out.println("достали юзера"+user);
        user.setTask(task);
        System.out.println("добавили task");
        tr.commit();
        System.out.println("tasks сохранен");

    }

    public synchronized List<User> getManagers(){
        EntityTransaction tr = em.getTransaction();
        TypedQuery<User> query = em.createNamedQuery("User.getManagers",User.class);
        tr.begin();
        List<User> users = query.getResultList();
        tr.commit();
        return users;
    }
}
