package entitys;

import java.io.Serializable;

/**
 * Created by kuteynikov on 29.06.2017.
 */

public class User implements Serializable{
    private final long userID;
    private String userName;
    private String firstName;
    private String LastName;
    private String typeUser = "customer";

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public User(long userID) {
        this.userID = userID;
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
