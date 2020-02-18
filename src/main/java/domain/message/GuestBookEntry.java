package domain.message;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "\"guest_book_entries\"")
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@JsonRootName(value = "message")
public class GuestBookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "POSTER_ID")
    private Integer posterId;

    @Builder.Default
    @Column(name = "PARENT_MSG_ID")
    private Integer parentMsgId = null;

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Basic(optional = false)
    @Column(name = "\"timestamp\"")
    private LocalDateTime timeStamp;

    @SuppressWarnings("unused")
    public GuestBookEntry() {
        this.timeStamp = LocalDateTime.now();
    }
}
