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
public class PageDto {
    @Valid
    private List<GuestBookEntryDto> entries = new ArrayList<>();
    private int totalPages;

    public GuestBookEntryDto convertAndAddEntity(GuestBookEntry entry) {
        GuestBookEntryDto newEntryDto = new GuestBookEntryDto(entry);
        this.entries.add(newEntryDto);
        return newEntryDto;
    }
}
