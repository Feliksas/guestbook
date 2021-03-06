package dto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import domain.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "users")
@JsonRootName(value = "users")
public class UserListDTO {
    @Valid
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "user")
    @JsonValue
    private List<UserDTO> users = new ArrayList<>();

    public void addUser(User user) {
        this.users.add(new UserDTO(user));
    }
}
