package form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackForm {
    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    @Email
    @Size(min = 6, max = 255)
    private String email;

    @NotNull
    @Size(min = 3, max = 65535)
    private String feedback;
}
