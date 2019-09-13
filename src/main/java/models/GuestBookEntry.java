package models;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="\"guest_book_entries\"")
public class GuestBookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    @Basic(optional = false)
    private String content;

    @Basic(optional = false)
    @Column(name = "\"timestamp\"")
    private LocalDateTime timeStamp;

    public GuestBookEntry() {
        this.timeStamp = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTimeStamp(LocalDateTime timeStamp) { this.timeStamp = timeStamp; }

    public LocalDateTime getTimeStamp() { return timeStamp; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

}
