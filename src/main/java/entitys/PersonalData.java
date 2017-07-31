package entitys;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @CollectionTable()
    private List<Long> referalsForPrize;
    private int countPrize=0;
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
        return this.advcashWallet;
    }

    public void setAdvcashWallet(String advcashWallet) {
        this.advcashWallet = advcashWallet;
    }

    public BigDecimal getLocalWallet() {
        return localWallet;
    }


    public String getUserNameTelegram() {
        return userNameTelegram;
    }

    public void setUserNameTelegram(String userNameTelegram) {
        this.userNameTelegram = userNameTelegram;
    }

    public List<Long> getReferalsForPrize() {
        if (this.referalsForPrize ==null)
            this.referalsForPrize =new ArrayList<>();
        return referalsForPrize;
    }

    public void addReferalForPrize(long userId){
        getReferalsForPrize().add(userId);
    }

    public int getCountPrize() {
        return countPrize;
    }

    public void incrementCountPrize() {
        this.countPrize++;
    }
}
