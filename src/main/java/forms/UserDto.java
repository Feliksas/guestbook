package forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.auth.Role;
import models.auth.User;

@Data
@AllArgsConstructor
public class UserDto {
    private int id;

    @NotNull
    @Size(min=3, max=255)
    private String userName;

    @Size(min=3, max=255)
    private String displayName;

    private String password;

    @NotNull
    @Email
    @Size(min=6,max=255)
    private String email;

    @NotNull
    private Boolean active;

    private boolean delete;

    @NotNull
    private List<String> roles;

    public UserDto(User user) {
        this.id = user.getId();
        this.userName = getUserName();
        this.displayName = user.getDisplayName();
        this.password = null;
        this.email = user.getEmail();
        this.active = user.isActive();
        this.delete = false;
        this.roles = new ArrayList<>();
        for (Role roleObj: user.getRoles()) {
            roles.add(roleObj.getRole());
        }
    }

    public boolean equals(User user) {
        return user.getId() == this.id;
    }
}
