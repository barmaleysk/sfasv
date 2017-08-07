package entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Services implements Serializable {
    @Id @GeneratedValue
    private int id;
    private LocalDateTime endDateOfSubscription;
    private Boolean onetimeConsultation=false;
    private Boolean unlimitSubscription=false;
    @Column(name = "deletedInMainChat")
    private Boolean deletedInMainChat = false;

     public Services() {
    }

    public LocalDateTime getEndDateOfSubscription() {
        return endDateOfSubscription;
    }

    public void setEndDateOfSubscription(LocalDateTime endDateOfSubscription) {
        this.endDateOfSubscription = endDateOfSubscription;
    }

    public Boolean getOnetimeConsultation() {
        return onetimeConsultation;
    }

    public void setOnetimeConsultation(Boolean onetimeConsultation) {
        this.onetimeConsultation = onetimeConsultation;
    }

    public Boolean getUnlimit() {
        return unlimitSubscription;
    }

    public void setUnlimit(Boolean unlimit) {
        unlimitSubscription = unlimit;
    }

    public void setDeletedInMainChat(Boolean deletedInMainChat) {
        this.deletedInMainChat = deletedInMainChat;
    }

    public Boolean getDeletedInMainChat() {
        return deletedInMainChat;
    }
}
