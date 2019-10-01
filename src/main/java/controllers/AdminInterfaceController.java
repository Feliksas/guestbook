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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @ModelAttribute("usersTable")
    public UserListDto listUsers() {
        List<UserDto> dtoUsers = new ArrayList<>();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));
        for (User user: dbUsers) {
            dtoUsers.add(new UserDto(user));
        }
        return new UserListDto(dtoUsers);
    }

    @ModelAttribute("availableRoles")
    public List<Role> listRoles() { return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "role"));}

    @GetMapping(path="/admin")
    public String showAdminPanel(Model model) {
        return ADMIN_VIEW;
    }

    @PostMapping(path="/admin")
    public String saveUsers(@Valid UserListDto form, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return ADMIN_VIEW;
        }
        List<UserDto> dtoUsers = form.getUsers();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));

        List<User> toDelete = new ArrayList<>();
        List<User> toUpdate = new ArrayList<>();

        for (User dbUser: dbUsers) {
            UserDto dtoUser = dtoUsers.stream()
                .filter(userDto -> userDto.equals(dbUser))
                .findAny()
                .orElse(null);
            if (dtoUser != null){ // modifying existing user
                dbUser.setUserName(dtoUser.getUserName());
                dbUser.setDisplayName(dtoUser.getDisplayName());
                dbUser.setEmail(dtoUser.getEmail());
                String rawPassword = dtoUser.getPassword();
                if (rawPassword = null)
            }
        }

        //userRepository.saveAll(form.getUsers());
        return ADMIN_VIEW;
    }
}