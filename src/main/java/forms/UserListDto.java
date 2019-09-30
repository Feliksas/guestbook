package forms;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.auth.User;

@Data
@AllArgsConstructor
public class UserListDto {
    private List<User> users;

    public void addUser(User user) {
        this.users.add(user);
    }
}
