package entitys;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kuteynikov on 29.06.2017.
 */
@Entity
@Table(name = "users")
public class User implements Serializable{
    @Id @NotNull
    private  long userID;
    private String userName;
    private String firstName;
    private String LastName;
    private String typeUser = "customer";
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    User() {}

    public User(long userID) {
        this.userID = userID;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
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
}