package controllers;

import javax.validation.Valid;
import java.util.List;
import forms.UserDto;
import forms.UserListDto;
import lombok.extern.slf4j.Slf4j;
import models.auth.Role;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import repository.auth.RoleRepository;
import repository.auth.UserRepository;
import service.UserDTOService;

@Controller
@Slf4j
public class AdminInterfaceController {
    private static final String ADMIN_VIEW = "admin";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserDTOService userDTOService;

    @PostMapping(path="/admin")
    @Transactional
    public String saveUsers(@Valid UserListDto form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return ADMIN_VIEW;
        }

        List<UserDto> dtoUsers = form.getUsers();
        userDTOService.persistDtoList(dtoUsers);

        return "redirect:/admin";
    }

    @GetMapping(path="/admin")
    public String showAdminPanel(Model model) {
        model.addAttribute("usersTable", listUsers());
        model.addAttribute("availableRoles", Role.getAllRoles());
        return ADMIN_VIEW;
    }

    private UserListDto listUsers() {
        UserListDto dtoUsers= new UserListDto();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));
        for (User user: dbUsers) {
            dtoUsers.addUser(user);
        }
        return dtoUsers;
    }
}