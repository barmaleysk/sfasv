package entitys;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "signals")
public class Signal implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime publishdateTime;

     Signal() {
    }

    public Signal(String text, LocalDateTime dateTime) {
        this.content = text;
        this.publishdateTime = dateTime;
    }

    public String getText() {
        return content;
    }

    public LocalDateTime getDateTime() {
        return publishdateTime;
    }
}
