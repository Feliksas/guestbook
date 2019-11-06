package forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.messages.GuestBookEntry;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookEntryDto {
    private int id;

    private String name = null;

    private String email = null;

    private LocalDateTime timeStamp;

    private Integer parentMsgId = null;

    private Integer posterId = -1;

    private List<GuestBookEntryDto> replies = new ArrayList<>();

    @Size(max=65535)
    @NotNull
    private String content;

    public GuestBookEntryDto(GuestBookEntry entry) {
        this.id = entry.getId();
        this.name = entry.getName();
        this.email = entry.getEmail();
        this.content = entry.getContent();
        this.timeStamp = entry.getTimeStamp();
        this.parentMsgId = entry.getParentMsgId();
        this.posterId = entry.getPosterId();
    }

    public void addEntry(GuestBookEntryDto entry) {
        this.replies.add(entry);
    }
}
