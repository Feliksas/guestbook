package controller;

import javax.validation.Valid;
import java.util.List;
import domain.auth.Role;
import dto.UserDTO;
import dto.UserListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    RoleRepository roleRepository;
    @Autowired
    UserDTOService userDTOService;
    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("SpringMVCViewInspection")
    @PostMapping(path = "/admin")
    @Transactional
    public String saveUsers(@Valid UserListDTO form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return ADMIN_VIEW;
        }

        List<UserDTO> dtoUsers = form.getUsers();
        userDTOService.persistDTOList(dtoUsers);

        return "redirect:/admin";
    }

    @GetMapping(path = "/admin")
    public String showAdminPanel(Model model) {
        model.addAttribute("usersTable", userDTOService.listUsers());
        model.addAttribute("availableRoles", Role.getAllRoles());
        return ADMIN_VIEW;
    }
}