package forms;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.messages.GuestBookEntry;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookEntriesListDto {
    @Valid
    private List<GuestBookEntryDto> entries = new ArrayList<>();
    private List<Integer> pages = null;

    public void addEntry(GuestBookEntry entry) {
        this.entries.add(new GuestBookEntryDto(entry));
    }
}
