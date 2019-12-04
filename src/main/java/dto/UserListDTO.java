package dto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import domain.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDTO {
    @Valid
    private List<UserDTO> users = new ArrayList<>();

    public void addUser(User user) {
        this.users.add(new UserDTO(user));
    }
}
