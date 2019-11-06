package controllers;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import forms.UserDto;
import forms.UserListDto;
import lombok.extern.slf4j.Slf4j;
import models.auth.Role;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    void fromDtoToEntity (UserDto dto, User entity) {
        entity.setUserName(dto.getUserName());
        entity.setDisplayName(dto.getDisplayName());
        entity.setEmail(dto.getEmail());
        entity.setActive(dto.getActive());

        String rawPassword = dto.getPassword();
        if (rawPassword != null && rawPassword != "") {
            entity.setPassword(new SCryptPasswordEncoder().encode(rawPassword));
        }

        List<String> dtoRoles = dto.getRoles();

        for (String rawRole : dtoRoles) {
            Role dbRole = Role.getRoleByName(rawRole);
            if (dbRole != null) { // such a role, indeed, exists in the system
                entity.getRoles().add(dbRole);
            }
        }

        List<Role> rolesToRemove = new ArrayList<>();
        for (Role userRole: entity.getRoles()) {
            if (!dtoRoles.contains(userRole.getRole())) {
                rolesToRemove.add(userRole);
            }
        }

        entity.getRoles().removeAll(rolesToRemove);
    }

    private UserListDto listUsers() {
        UserListDto dtoUsers= new UserListDto();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));
        for (User user: dbUsers) {
            dtoUsers.addUser(user);
        }
        return dtoUsers;
    }

    @GetMapping(path="/admin")
    public String showAdminPanel(Model model) {
        model.addAttribute("usersTable", listUsers());
        model.addAttribute("availableRoles", Role.getAllRoles());
        return ADMIN_VIEW;
    }

    @PostMapping(path="/admin")
    @Transactional
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
        toDelete.forEach(dbUsers::remove);

        // now we should only have new users in DTO
        for (UserDto dtoUser: dtoUsers) {
            User newUser = new User();
            fromDtoToEntity(dtoUser, newUser);
            dbUsers.add(newUser);
        }

        userRepository.saveAll(dbUsers);
        userRepository.deleteAll(toDelete);

        return "redirect:/admin";
    }
}