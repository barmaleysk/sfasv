package entitys;

import org.apache.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task implements Serializable {
    @Transient
    private static final Logger log = Logger.getLogger(Task.class);
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String type;
    private String status;
    @ManyToMany(fetch = FetchType.EAGER) @Size(max = 2)
    private List<User> users;
    private LocalDateTime dateTimeOpening;
    private LocalDateTime dateTimeEnding;

     Task() {
    }

    public Task(String type, User user) {
        this.type = type;
        this.status = TaskStatus.OPEN;
        this.users = new ArrayList<>();
        this.users.add(user);
        this.dateTimeOpening = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public User getClient() {
         User client;
        if (users!=null&&users.size()==2){
            User user1 = users.get(0);
            User user2 = users.get(1);
            if (user1.getTypeUser().equals("customer"))
                client=user1;
            else
                client=user2;
        }
        else if (users!=null&&users.size()==1)
            client=users.get(0);
        else
            client=null;
        return client;
    }

    public User getMeneger() {
         User manager=null;
         if (users!=null&&users.size()==2){
             User user1 = users.get(0);
             User user2 = users.get(1);
             if (user1.getTypeUser().equals("manager"))
                 manager=user1;
             else
                 manager=user2;
         }
        return manager;
    }

    public LocalDateTime getDateTimeOpening() {
        return dateTimeOpening;
    }

    public LocalDateTime getDateTimeEnding() {
        return dateTimeEnding;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMeneger(User manager) {
        if (this.users!=null&&users.size()==1)
            users.add(manager);
        else
            log.error("Не смог добавить менеджера в task="+this.id);
    }

    public void setDateTimeEnding(LocalDateTime dateTimeEnding) {
        this.dateTimeEnding = dateTimeEnding;
    }

    @Override
    public String toString() {
         String managerNick="-";
         if (getMeneger()!=null)
             managerNick=getMeneger().getPersonalData().getUserNameTelegram();
        return "Id заявки: "+this.id
                +"\nТип: "+this.type
                +"\nДата создания: "+this.dateTimeOpening
                +"\nUserId: "+getClient().getUserID()
                +"\nUserName: "+getClient().getPersonalData().getUserNameTelegram()
                +"\nVIP: "+getClient().getServices().getUnlimit()
                +"\nОбрабатывает: "+managerNick;

    }
}
