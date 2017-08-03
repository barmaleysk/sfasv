package database_service;

import entitys.*;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class DbService {
    private static final Logger log = Logger.getLogger(DbService.class);
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
        em.refresh(userFromDb);
        transaction.commit();
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

    public synchronized List<Long> getSubscribers(){
        Query query = em.createQuery("SELECT u.userID FROM User u JOIN u.services s  WHERE s.endDateOfSubscription>=:d AND s.unlimitSubscription=:b")
                .setParameter("d", LocalDateTime.now())
                .setParameter("b",true);
        List<Long> usersId =null;
       // tr.begin();
        usersId = query.getResultList();
        //tr.commit();
        em.clear();
        System.out.println("usersId:"+usersId);
        System.out.println("usersId size="+usersId.size());
        return usersId;
    }

    public synchronized List<Tasks> getTasks(String status, String type){
        List<Tasks> tasks;
        EntityTransaction tr = em.getTransaction();
        Query query = em.createQuery("SELECT t FROM Tasks t WHERE t.status=:s AND t.type=:v")
                .setParameter("s", status)
                .setParameter("v",type);
        tr.begin();
        tasks = query.getResultList();
        tr.commit();
        return tasks;
    }

    public void updatePersonalData(String firstName, String lastName, String userName, Long Id) {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,Id);
        user.getPersonalData().setFirstName(firstName);
        user.getPersonalData().setLastName(lastName);
        user.getPersonalData().setUserNameTelegram(userName);
        tr.commit();
    }

    public synchronized List<Long> getUnSubscriptionUsers(){
        Query query = em.createQuery("SELECT u.userID FROM User u JOIN u.services s  WHERE s.endDateOfSubscription<=:d AND s.unlimitSubscription=:b ")
                .setParameter("d",LocalDateTime.now())
                .setParameter("b",false);
        List<Long> usersId = query.getResultList();
        em.clear();
        System.out.println("usersId:" +usersId);
        return usersId;
    }

    public Tasks closeTask(Long idTask, Long mangerId) throws NoTaskInDb, NoUserInDb {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        Tasks task = em.find(Tasks.class,idTask);
        User manager = em.find(User.class,mangerId);
        User client = em.find(User.class,task.getClient().getUserID());
        if (task==null)
            throw new NoTaskInDb();
        else if (manager==null)
            throw new NoUserInDb();
        else {
            //если заявка на выплату то добавляем локальную тразакцию и списываем деньги
            if (task.getType().equals(TaskType.PAY_PRIZE)||task.getType().equals(TaskType.PAY_BONUSES)) {
                LocalTransaction localTransaction = new LocalTransaction(
                        LocalDateTime.now(),
                        //чтобы записать типа -10.00  из 0 вычитаем значение кошелька
                        new BigDecimal("0.00").subtract(task.getClient().getPersonalData().getLocalWallet()),
                        client
                );
                client.addLocalTransactions(localTransaction);
                client.getPersonalData().setLocalWallet(new BigDecimal("0.00"));
                log.info("выполнен вывод средств для "+client);
            }
            task.setStatus(TaskStatus.CLOSE);
            task.setDateTimeEnding(LocalDateTime.now());
            if(task.getClient().getUserID()!=manager.getUserID())
               task.setMeneger(manager);
            else
                log.error("Ошибка при закрытии заявки UserId менеджера и клиента одинаковы="+mangerId);

        }
        tr.commit();
        log.info("закрыта заявка для "+client);
        return task;
    }
}
