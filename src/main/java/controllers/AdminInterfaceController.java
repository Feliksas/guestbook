package controllers;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import forms.UserDto;
import forms.UserListDto;
import lombok.extern.slf4j.Slf4j;
import models.auth.Role;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import repository.auth.RoleRepository;
import repository.auth.UserRepository;

@Controller
@Slf4j
public class AdminInterfaceController {
    private static final String ADMIN_VIEW = "admin";

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private void fromDtoToEntity (UserDto dto, User entity) {
        Set<Role> newRoles = new HashSet<>();

        entity.setUserName(dto.getUserName());
        entity.setDisplayName(dto.getDisplayName());
        entity.setEmail(dto.getEmail());
        entity.setActive(dto.getActive());
        entity.setRoles(newRoles);

        String rawPassword = dto.getPassword();
        if (rawPassword != null) {
            entity.setPassword(new SCryptPasswordEncoder().encode(rawPassword));
        }

        for (String rawRole : dto.getRoles()) {
            Role dbRole = roleRepository.findByRole(rawRole);
            if (dbRole != null) {
                entity.getRoles().add(dbRole);
                roleRepository.save(dbRole);
            }
        }
    }

    private UserListDto listUsers() {
        List<UserDto> dtoUsers = new ArrayList<>();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));
        for (User user: dbUsers) {
            dtoUsers.add(new UserDto(user));
        }
        UserListDto userList = new UserListDto(dtoUsers);
        log.debug(userList.toString());
        return userList;
    }

    List<Role> listRoles() { return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "role"));}

    @GetMapping(path="/admin")
    public String showAdminPanel(Model model) {
        model.addAttribute("usersTable", listUsers());
        model.addAttribute("availableRoles", listRoles());
        return ADMIN_VIEW;
    }

    @PostMapping(path="/admin")
    public String saveUsers(@Valid UserListDto form, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return ADMIN_VIEW;
        }

        log.debug(model.toString());
        log.debug(form.toString());
        List<UserDto> dtoUsers = form.getUsers();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));

        List<User> toDelete = new ArrayList<>();

        for (User dbUser: dbUsers) { // check if we need to modify existing users
            UserDto dtoUser = dtoUsers.stream()
                .filter(userDto -> userDto.equals(dbUser))
                .findAny()
                .orElse(null);
            if (dtoUser != null) { // modifying existing user
                if (dtoUser.isDelete()) { // marked on form for deletion
                    toDelete.add(dbUser);
                    dtoUsers.remove(dtoUser);
                } else {
                    fromDtoToEntity(dtoUser, dbUser);
                    dtoUsers.remove(dtoUser);
                }
            }
        }

        // remove users marked for deletion from active set
        for (User user: toDelete) {
            dbUsers.remove(user);
        }

        // now we should only have new users in DTO
        for (UserDto dtoUser: dtoUsers) {
            User newUser = new User();
            fromDtoToEntity(dtoUser, newUser);
            dbUsers.add(newUser);
        }

        userRepository.saveAll(dbUsers);
        userRepository.deleteAll(toDelete);

        return ADMIN_VIEW;
    }
}