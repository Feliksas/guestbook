package forms;

import javax.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserListDto {
    @Valid
    private List<UserDto> users;
}
