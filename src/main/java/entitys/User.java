package entitys;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
                query = "SELECT u FROM User u WHERE u.userID=:k")
})
public class User implements Serializable{
    @Id
    private  long userID;
    private long chatID;
    private int level;
    private int rightKey;
    private int leftKey;

    private String userName;
    private String firstName;
    private String LastName;
    private int phoneNumber;
    private String email;

    private String typeUser = "customer";

    private LocalDateTime endDateOfSubscription;
    private String VipConsultation;

    @Column(scale = 2,precision = 10)
    private BigDecimal localWallet;
    private String advcashWallet;
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
        this.userName =userName;
        this.firstName=firstName;
        this.LastName = lastName;
        this.chatID =chatID;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public LocalDateTime getEndDateOfSubscription() {
        return endDateOfSubscription;
    }

    public void setEndDateOfSubscription(LocalDateTime endDateOfSubscription) {
        this.endDateOfSubscription = endDateOfSubscription;
    }




    public BigDecimal getLocalWallet() {
        return this.localWallet==null ? new BigDecimal("0.00") : this.localWallet;
    }

    public void setLocalWallet(BigDecimal localWallet) {
        this.localWallet = localWallet;
    }

    public String getAdvcashWallet() {
        return advcashWallet;
    }

    public void setAdvcashWallet(String advcashWallet) {
        this.advcashWallet = advcashWallet;
    }

    public List<AdvcashTransaction> getAdvcashTransactions() {
        return advcashTransactions;
    }

    public List<LocalTransaction> getLocalTransactions() {
        return localTransactions;
    }


    public void addLocalTransactions(LocalTransaction localTransaction) {
        if (this.localTransactions==null)
            localTransactions= new ArrayList<>();
        this.localTransactions.add(localTransaction);
    }

    public void addAcTransaction(AdvcashTransaction transaction){
        if (advcashTransactions==null)
            this.advcashTransactions=new ArrayList<>();
        advcashTransactions.add(transaction);
    }

    @Override
    public String toString() {
        return "Имя: "+getFirstName()
                +"| Фамилия: "+getLastName()
                +"| UserName: "+getUserName()
                +"| UserID: "+getUserID()
                +"| Тип: "+getTypeUser()
                +"| конец подписки: "+endDateOfSubscription;
    }


}
