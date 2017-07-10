package entitys;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Dfyz on 09.07.2017.
 */
@Entity
@Table(name = "referals")
@NamedQueries({
        @NamedQuery(name = "Referal.calculateKeyStep1",
                query = "UPDATE Referal r SET r.leftKey=r.leftKey+2, r.rightKey=r.rightKey+2 WHERE r.leftKey>:key"),
        @NamedQuery(name = "Referal.calculateKeyStep2",
                query = "UPDATE Referal r SET r.rightKey=r.rightKey+2 WHERE r.rightKey>=:key AND r.leftKey<:key"),
        @NamedQuery(name = "Referal.getChildren",
                query = "SELECT r FROM Referal r WHERE r.leftKey>=:key AND r.rightKey<=:key")
})
public class Referal implements Serializable{
    @Id @NotNull
    private long userID;
    private int level;
    private int rightKey;
    private int leftKey;

    public Referal() {
    }

    public Referal(long userID) {
        this.userID = userID;
    }

    public Referal(long userID, int level, int rightKey, int leftKey) {
        this.userID = userID;
        this.level = level;
        this.rightKey = rightKey;
        this.leftKey = leftKey;
    }

    public long getUserID() {
        return userID;
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
}
