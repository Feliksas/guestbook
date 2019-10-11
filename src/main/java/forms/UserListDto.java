package forms;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.auth.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto {
    @Valid
    private List<UserDto> users = new ArrayList<>();

    public void addUser(User user) {
        this.users.add(new UserDto(user));
    }
}
