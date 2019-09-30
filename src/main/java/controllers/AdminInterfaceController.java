package controllers;

import java.util.ArrayList;
import java.util.List;
import forms.UserListDto;
import models.auth.Role;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import repository.auth.RoleRepository;
import repository.auth.UserRepository;

@Controller
public class AdminInterfaceController {
    private static final String ADMIN_VIEW = "admin";

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @ModelAttribute("usersTable")
    public UserListDto listUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName")).iterator().forEachRemaining(users::add);
        return new UserListDto(users);
    }

    @ModelAttribute("availableRoles")
    public List<Role> listRoles() { return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "role"));}

    @GetMapping(path="/admin")
    public String showAdminPanel(Model model) {
        return ADMIN_VIEW;
    }

    @PostMapping(path="/admin")
    public String saveUsers(@ModelAttribute UserListDto form, Model model) {
        userRepository.saveAll(form.getUsers());
        return ADMIN_VIEW;
    }
}
