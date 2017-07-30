package entitys;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuteynikov on 29.06.2017.
 */
@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.calculateKeyStep1",
                query = "UPDATE User u SET u.leftKey=u.leftKey+2, u.rightKey=u.rightKey+2 WHERE u.leftKey>:key"),
        @NamedQuery(name = "User.calculateKeyStep2",
                query = "UPDATE User u SET u.rightKey=u.rightKey+2 WHERE u.rightKey>=:key AND u.leftKey<:key"),
        @NamedQuery(name = "User.getChildren",
                query = "SELECT u FROM User u WHERE u.leftKey>=:key AND u.rightKey<=:key"),
        @NamedQuery(name = "User.getMaxRightKey",
                query = "SELECT MAX(u.rightKey) FROM User u"),
        @NamedQuery(name = "User.getAllChildren",
                query = "SELECT u FROM User u WHERE u.leftKey>:lk AND u.rightKey<:rk AND u.level>:l AND u.level<:l+4"),
        @NamedQuery(name = "User.getUser",
                query = "SELECT u FROM User u WHERE u.userID=:k"),
        @NamedQuery(name = "User.getManagers",
                query = "SELECT u FROM User u WHERE u.typeUser='manager'"),
})
public class User implements Serializable{
    @Id
    private  long userID;
    private long chatID;
    private int level;
    private int rightKey;
    private int leftKey;
    private String typeUser = "customer";

    @ManyToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Tasks> tasks;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Services services;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PersonalData personalData;

    @ManyToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<AdvcashTransaction> advcashTransactions;
    @ManyToMany(mappedBy = "childrenUsers",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<LocalTransaction> localTransactions;

    User() {}

    public User(long userID) {
        this.userID = userID;
    }

    public User(long userID, String userName, String firstName, String lastName, long chatID) {
        this.userID=userID;
        getPersonalData().setUserNameTelegram(userName);
        getPersonalData().setFirstName(firstName);
        getPersonalData().setLastName(lastName);
        this.chatID=chatID;
    }

    public long getUserID() {
        return userID;
    }

    public long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRightKey() {
        return rightKey;
    }

    public void setRightKey(int rightKey) {
        this.rightKey = rightKey;
    }

    public int getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(int leftKey) {
        this.leftKey = leftKey;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public Services getServices() {
        if (this.services==null)
            this.services= new Services();
        return services;
    }

    public PersonalData getPersonalData() {
        if (this.personalData==null)
            this.personalData=new PersonalData();
        return this.personalData;
    }

    public List<Tasks> getTasks() {
        return this.tasks;
    }

    public void setTask(Tasks task) {
        if (this.tasks==null)
            this.tasks=new ArrayList<>();
        this.tasks.add(task);
    }

    public void setEndDateOfSubscription(LocalDateTime endDateOfSubscription) {
        Services services = getServices();
        services.setEndDateOfSubscription(endDateOfSubscription);
    }

    public BigDecimal getLocalWallet() {
        PersonalData personalData = getPersonalData();
        return personalData.getLocalWallet();
    }

    public String getUserName() {
        return getPersonalData().getUserNameTelegram();
    }

    public String getFirstName() {
        return getPersonalData().getFirstName();
    }

    public String getAdvcashWallet() {
        return getPersonalData().getAdvcashWallet();
    }

    public LocalDateTime getEndDateOfSubscription() {
        return getServices().getEndDateOfSubscription();
    }

    public List<AdvcashTransaction> getAdvcashTransactions() {
        return this.advcashTransactions;
    }

    public Tasks getCurrentTasks(String taskType){
        Tasks task=null;
        if (this.tasks!=null){
            for (Tasks t : this.tasks){
                if (!t.getStatus().equals(TaskStatus.CLOSE))
                    task=t;
            }
        }
        return task;
    }

    @Override
    public String toString() {
        return " UserID: "+getUserID()
                +"| Тип: "+getTypeUser();
    }



}
