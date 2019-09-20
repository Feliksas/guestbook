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

    public GuestBookEntry withId(Integer id) {
        this.id = id;

        return this;
    }

    public GuestBookEntry withTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;

        return this;
    }

    public LocalDateTime getTimeStamp() { return timeStamp; }

    public String getName() {
        return name;
    }

    public GuestBookEntry withName(String name) {
        this.name = name;

        return this;
    }

    public String getEmail() {
        return email;
    }

    public GuestBookEntry withEmail(String email) {
        this.email = email;

        return this;
    }

    public String getContent() { return content; }

    public GuestBookEntry withContent(String content) {
        this.content = content;

        return this;
    }

}
