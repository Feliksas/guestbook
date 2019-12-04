package dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import domain.message.GuestBookEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookEntryDTO {
    private Integer id = null;

    private String name = null;

    private String email = null;

    private LocalDateTime timeStamp;

    private Integer parentMsgId = null;

    private Integer posterId = -1;

    private List<GuestBookEntryDTO> replies = new ArrayList<>();

    @Size(max = 65535)
    @NotNull
    private String content;

    public GuestBookEntryDTO(GuestBookEntry entry) {
        this.id = entry.getId();
        this.name = entry.getName();
        this.email = entry.getEmail();
        this.content = entry.getContent();
        this.timeStamp = entry.getTimeStamp();
        this.parentMsgId = entry.getParentMsgId();
        this.posterId = entry.getPosterId();
    }

    public void addEntry(GuestBookEntryDTO entry) {
        this.replies.add(entry);
    }
}
