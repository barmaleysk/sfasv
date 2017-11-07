package database_service;

import entitys.*;
import org.apache.log4j.Logger;
import telegram_services.CommandButtons;
import telegram_services.exceptions.AlreadyClosenTaskException;
import telegram_services.exceptions.AlreadyHadlingTaskException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class DbService {
    private static final Logger log = Logger.getLogger(DbService.class);
    private static DbService dbService;
    private final EntityManagerFactory entityManagerFactory;
    private DbService() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("eclipsMysql");
    }

    public static DbService getInstance(){
        if (dbService==null)
            dbService=new DbService();
        return dbService;
    }

    public synchronized void addRootUser(User user){
        EntityManager em = entityManagerFactory.createEntityManager();
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
        em.close();
    }

    public synchronized User getUserFromDb(long userId){
        EntityManager em = entityManagerFactory.createEntityManager();
        User userFromDb = em.find(User.class,userId);
        em.clear();
        em.close();
        return userFromDb;
    }

    public synchronized boolean dbHasUser(long userId){
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User user = em.find(User.class,userId);
        transaction.commit();
        boolean check = false;
        if (user!=null)
            check=true;
        em.close();
        return check;
    }

    public synchronized void addChildrenUser (long parentUserId,User childrenUser) throws NoUserInDb {
        EntityManager em = entityManagerFactory.createEntityManager();
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
        em.close();
    }

    public synchronized List<User> getChildrenUsers(int parentLevel,int parenLeftKey, int parentRightKey ){
        EntityManager em = entityManagerFactory.createEntityManager();
        List<User> usersList;
        TypedQuery<User> query = em.createNamedQuery("User.getAllChildren",User.class);
        query.setParameter("rk",parentRightKey);
        query.setParameter("lk",parenLeftKey);
        query.setParameter("l",parentLevel);

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        usersList=query.getResultList();
        transaction.commit();
        em.close();
        return usersList;
    }

    public synchronized void addTask(long userID, Task task) {
        EntityManager em = entityManagerFactory.createEntityManager();
        System.out.println("сохраняем tasks");
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,userID);
        System.out.println("достали юзера"+user);
        user.setTask(task);
        System.out.println("добавили task");
        tr.commit();
        em.close();
        System.out.println("tasks сохранен");

    }

    public synchronized List<User> getManagers(){
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        TypedQuery<User> query = em.createNamedQuery("User.getManagers",User.class);
        tr.begin();
        List<User> users = query.getResultList();
        tr.commit();
        em.close();
        return users;
    }

    public synchronized List<Long> getSubscribers(){
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u.userID FROM User u JOIN u.services s  WHERE s.endDateOfSubscription>:d OR s.unlimitSubscription=:b")
                .setParameter("d", LocalDateTime.now())
                .setParameter("b",true);
        List<Long> usersId =null;
       // tr.begin();
        usersId = query.getResultList();
        //tr.commit();
        em.clear();
        System.out.println("usersId:"+usersId);
        System.out.println("usersId size="+usersId.size());
        em.close();
        return usersId;
    }

    public synchronized List<Task> getTasks(String status, String type){
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Task> tasks;
        EntityTransaction tr = em.getTransaction();
        Query query = em.createQuery("SELECT t FROM Task t WHERE t.status<>:s AND t.type=:v")
                .setParameter("s", status)
                .setParameter("v",type);
        tr.begin();
        System.out.println("Ищем заявки");
        tasks = query.getResultList();
        System.out.println("Нашли заявки "+tasks);
        tr.commit();
        em.close();
        return tasks;
    }

    public void updatePersonalData(String firstName, String lastName, String userName, Long Id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,Id);
        user.getPersonalData().setFirstName(firstName);
        user.getPersonalData().setLastName(lastName);
        user.getPersonalData().setUserNameTelegram(userName);
        tr.commit();
        em.close();
        log.info("в базе обновлены данные для "+user);
    }

    public synchronized List<Long> getUnSubscriptionUsers(){
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u.userID FROM User u JOIN u.services s  WHERE s.endDateOfSubscription<=:d AND s.unlimitSubscription<>:b AND s.deletedInMainChat<>:r")
                .setParameter("d",LocalDateTime.now())
                .setParameter("b",true)
                .setParameter("r",true);
        List<Long> usersId = query.getResultList();
        em.clear();
        em.close();
        return usersId;
    }

    public synchronized Task closeTask(Long idTask, Long mangerId) throws NoTaskInDb, NoUserInDb, AlreadyClosenTaskException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        Task task = em.find(Task.class,idTask);
        User manager = em.find(User.class,mangerId);
        User client = em.find(User.class,task.getClient().getUserID());
        if (task==null)
            throw new NoTaskInDb();
        if (manager==null)
            throw new NoUserInDb();
        if (task.getStatus().equals(TaskStatus.CLOSE))
            throw  new AlreadyClosenTaskException();

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
                client.getPersonalData().setPrize(0);

                log.info("выполнен вывод средств для "+client);
            }
            task.setStatus(TaskStatus.CLOSE);
            task.setDateTimeEnding(LocalDateTime.now());
            if(task.getClient().getUserID()!=manager.getUserID())
               task.setMeneger(manager);
            else
                log.error("Ошибка при закрытии заявки UserId менеджера и клиента одинаковы="+mangerId);


        tr.commit();
        em.close();
        log.info("закрыта заявка для "+client);
        return task;
    }

    public synchronized void setDeletedMainChat(long userID, boolean b) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,userID);
        user.getServices().setDeletedInMainChat(b);
        tr.commit();
        em.close();
    }

    public synchronized void persistSignal(String textSignal) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        System.out.println("сохраняем сигнал");
        tr.begin();
        em.persist(new Signal(textSignal,LocalDateTime.now()));
        tr.commit();
        em.close();
        log.info("Сохранён сигнал");
    }

    public synchronized List<Signal> getSignals() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        Query query = em.createQuery("SELECT s FROM Signal s WHERE s.publishdateTime>=:t ORDER BY s.publishdateTime")
                .setParameter("t",LocalDateTime.now().minusDays(7));
        tr.begin();
        List<Signal> signals = query.getResultList();
        tr.commit();
        em.close();
        return signals;
    }

    public Task taskInWork(Long idTask, Long managerId) throws NoTaskInDb, NoUserInDb, AlreadyHadlingTaskException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        Task task = em.find(Task.class,idTask);
        System.out.println("вытащаили из базы задачу "+task);
        if (task==null)
            throw new NoTaskInDb();


        User manager = em.find(User.class,managerId);
        System.out.println("менеджер "+manager);
        User client = em.find(User.class,task.getClient().getUserID());
        System.out.println("клиент "+client);

        if (manager==null||client==null)
            throw new NoUserInDb();

        if (!task.getStatus().equals(TaskStatus.OPEN))
            throw new AlreadyHadlingTaskException();

        task.setStatus(TaskStatus.IN_WORK);

        if (manager.getUserID()==client.getUserID()){
            log.error("Ошибка при взятии в работу заявки, userId юзера и менеджера одинаковы ="+manager.getUserID());
        } else {
            task.setMeneger(manager);
        }
        tr.commit();
        em.close();
        return task;
    }

    public User setServices(Long clientId, CommandButtons commandButtons) throws NoUserInDb {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User client = em.find(User.class,clientId);
        if (client==null)
            throw new NoUserInDb();
        switch (commandButtons){
            case UNLIMIT:
                client.getServices().setUnlimit(true);
                break;
            default:
                break;
        }
        tr.commit();
        em.close();
        return client;
    }

    public void deleteUser(Long chatId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        Query query1 = em.createNamedQuery("User.delete1");
        Query query2 = em.createNamedQuery("User.delete2");
        tr.begin();
        User user = em.find(User.class,chatId);
        int rk = user.getRightKey();
        int lk = user.getLeftKey();

        query1.setParameter("rk",rk);
        tr.commit();
        em.close();
    }

    public void setAcWallet(Long chatId, String wallet) throws NoUserInDb {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,chatId);
        if (user==null)
            throw new NoUserInDb();
        user.getPersonalData().setAdvcashWallet(wallet);
        tr.commit();
        em.close();
    }

    public void updateUser(User user) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        try {
            em.merge(user);
            tr.commit();
        }catch (Exception e){
            tr.rollback();
            log.error("Ошибка при обновлении юзера "+user);
        }
        em.clear();
        em.close();
    }
}
