package form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationForm {
    @NotNull
    @Size(min = 3, max = 255)
    private String fullName;

    @NotNull
    @Size(min = 3, max = 255)
    private String userName;

    @NotNull
    @Email
    @Size(min = 6, max = 255)
    private String email;

    @NotNull
    @Size(min = 8, max = 255)
    private String password;
}
