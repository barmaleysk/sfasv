package entitys;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

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
                query = "SELECT MAX(u.rightKey) FROM User u")
})
public class User implements Serializable{
    @Id @NotNull
    private  long userID;
    private String userName;
    private String firstName;
    private String LastName;
    private String typeUser = "customer";
    private LocalDate endDate;
    private long chatID;
    private int level;
    private int rightKey;
    private int leftKey;

    User() {}

    public User(long userID, String userName, String firstName, String lastName,long chatID) {
        this.userID = userID;
        this.userName = userName;
        this.firstName = firstName;
        this.LastName = lastName;
        this.chatID = chatID;
    }

    public User(long userID) {
        this.userID = userID;
    }

    public long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }

    public long getUserID() {
        return userID;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
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

    @Override
    public String toString() {
        return "Имя: "+getFirstName()
                +"| Фамилия: "+getLastName()
                +"| UserName: "+getUserName()
                +"| UserID: "+getUserID()
                +"| Тип: "+getTypeUser()
                +"| конец подписки: "+getEndDate();
    }
}
