package dto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import domain.message.GuestBookEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {
    @Valid
    private List<GuestBookEntryDTO> entries = new ArrayList<>();
    private int totalPages;

    public GuestBookEntryDTO convertAndAddEntity(GuestBookEntry entry) {
        GuestBookEntryDTO newEntryDto = new GuestBookEntryDTO(entry);
        this.entries.add(newEntryDto);
        return newEntryDto;
    }
}
