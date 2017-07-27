package entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class PersonalData implements Serializable {
    @Id @GeneratedValue
    private long id;
    private String userNameTelegram;
    private int phoneNumber;
    private String email;
    private String nickVK;
    private String accountCryptoCompare;
    private String firstName;
    private String LastName;
    private String advcashWallet;
    @Column(scale = 2,precision = 10)
    private BigDecimal localWallet;

    public PersonalData() {
    }

    public long getId() {
        return id;
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

    public String getNickVK() {
        return nickVK;
    }

    public void setNickVK(String nickVK) {
        this.nickVK = nickVK;
    }

    public String getAccountCryptoCompare() {
        return accountCryptoCompare;
    }

    public void setAccountCryptoCompare(String accountCryptoCompare) {
        this.accountCryptoCompare = accountCryptoCompare;
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

    public String getAdvcashWallet() {
        return advcashWallet;
    }

    public void setAdvcashWallet(String advcashWallet) {
        this.advcashWallet = advcashWallet;
    }

    public BigDecimal getLocalWallet() {
        return localWallet;
    }

    public void setLocalWallet(BigDecimal localWallet) {
        this.localWallet = localWallet;
    }

    public String getUserNameTelegram() {
        return userNameTelegram;
    }

    public void setUserNameTelegram(String userNameTelegram) {
        this.userNameTelegram = userNameTelegram;
    }
}
