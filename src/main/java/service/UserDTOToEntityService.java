package service;

import java.util.ArrayList;
import java.util.List;
import domain.auth.Role;
import domain.auth.User;
import dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDTOToEntityService {

    public User convert(UserDTO fromDto) {
        User user = new User();
        convert(fromDto, user);
        return user;
    }

    public void convert(UserDTO fromDto, User toEntity) {
        copySimpleFields(fromDto, toEntity);
        copyPassword(fromDto, toEntity);
        copyRoles(fromDto, toEntity);
    }

    private void copySimpleFields(UserDTO fromDto, User toEntity) {
        toEntity.setUserName(fromDto.getUserName());
        toEntity.setDisplayName(fromDto.getDisplayName());
        toEntity.setEmail(fromDto.getEmail());
        toEntity.setActive(fromDto.getActive());
    }

    private void copyPassword(UserDTO fromDto, User toEntity) {
        String rawPassword = fromDto.getPassword();
        if (rawPassword != null && !rawPassword.equals("")) {
            toEntity.setPassword(new SCryptPasswordEncoder().encode(rawPassword));
        }
    }

    private void copyRoles(UserDTO fromDto, User toEntity) {
        copyExistedRoles(fromDto, toEntity);
        removeRoles(fromDto, toEntity);
    }

    private void copyExistedRoles(UserDTO fromDto, User toEntity) {
        List<String> dtoRoles = fromDto.getRoles();

        for (String rawRole : dtoRoles) {
            Role dbRole = Role.getRoleByName(rawRole);
            if (dbRole != null) { // such a role, indeed, exists in the system
                toEntity.getRoles().add(dbRole);
            } else {
                log.warn("Unknown role: " + rawRole);
            }
        }
    }

    private void removeRoles(UserDTO fromDto, User toEntity) {
        List<String> dtoRoles = fromDto.getRoles();

        List<Role> rolesToRemove = new ArrayList<>();
        for (Role userRole : toEntity.getRoles()) {
            if (!dtoRoles.contains(userRole.getRole())) {
                rolesToRemove.add(userRole);
            }
        }

        toEntity.getRoles().removeAll(rolesToRemove);
    }

}
