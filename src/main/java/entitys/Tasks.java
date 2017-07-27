package entitys;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tasks implements Serializable {
    @Id @GeneratedValue
    private long id;
    private String type;
    private String status;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> clients;
    @ManyToMany()
    private List<User> menegers;
    private LocalDateTime dateTimeOpening;
    private LocalDateTime dateTimeEnding;

     Tasks() {
    }

    public Tasks(String type, User client) {
        this.type = type;
        this.status = TaskStatus.OPEN;
        this.clients = new ArrayList<>();
        this.clients.add(client);
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
        return clients==null?null:clients.get(0);
    }

    public User getMeneger() {
        return menegers==null?null:menegers.get(0);
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

    public void setMeneger(User meneger) {
         if (this.menegers==null)
             this.menegers=new ArrayList<>();
        this.menegers.add(meneger);
    }

    public void setDateTimeEnding(LocalDateTime dateTimeEnding) {
        this.dateTimeEnding = dateTimeEnding;
    }
}
