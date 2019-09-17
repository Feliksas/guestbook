package forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FeedbackForm {
    @NotNull
    @Size(min=3, max=255)
    private String name;

    @NotNull
    @Email
    @Size(min=6,max=255)
    private String email;

    @NotNull
    @Size(min=3, max=65535)
    private String feedback;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
