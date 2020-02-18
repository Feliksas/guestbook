package dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import domain.auth.Role;
import domain.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName(value = "user")
@JacksonXmlRootElement(localName = "user")
public class UserDTO {
    private int id;

    @NotNull
    @Size(min = 3, max = 255)
    private String userName;

    @Size(min = 3, max = 255)
    private String displayName;

    @JsonIgnore
    @XmlTransient
    private String password;

    @NotNull
    @Email
    @Size(min = 6, max = 255)
    private String email;

    @NotNull
    private Boolean active;

    @JsonIgnore
    @XmlTransient
    private boolean delete;

    @NotNull
    @JacksonXmlProperty(localName = "role")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "roles")
    private List<String> roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.displayName = user.getDisplayName();
        this.password = null;
        this.email = user.getEmail();
        this.active = user.isActive();
        this.delete = false;
        this.roles = new ArrayList<>();
        for (Role roleObj : user.getRoles()) {
            roles.add(roleObj.getRole());
        }
    }
}
