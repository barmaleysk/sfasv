package entitys;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LocalTransactions")
public class LocalTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private LocalDateTime dateTime;
    @Column(scale = 2,precision = 10)
    private BigDecimal amount;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> childrenUsers;

     LocalTransaction() {
    }

    public LocalTransaction(LocalDateTime dateTime, BigDecimal amount, User childrenUser) {
        this.dateTime = dateTime;
        this.amount = amount;
        this.childrenUsers = new ArrayList<>();
        childrenUsers.add(childrenUser);
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public User getChildrenUser() {
        return childrenUsers==null? null : childrenUsers.get(0);
    }
}
