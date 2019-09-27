package controllers;

import javax.validation.Valid;
import exceptions.AccountExistsException;
import exceptions.EmailExistsException;
import forms.RegistrationForm;
import lombok.extern.slf4j.Slf4j;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.UserService;

@Controller
@Slf4j
public class RegistrationController {
    private static final String REGISTRATION_FORM = "register";

    private static final String USER_EXISTS = "userExists";
    private static final String EMAIL_EXISTS = "emailExists";

    private static final String MESSAGE_FAIL = "<strong>Error!</strong> Please check your input!";
    private static final String MESSAGE_ATTR = "submitResultMessage";

    @Autowired
    private UserService userService;

    @GetMapping(path="/register")
    public String newUserRegistration(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return REGISTRATION_FORM;
    }

    @PostMapping(path="/register")
    public String doRegister(@Valid RegistrationForm registrationForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            return REGISTRATION_FORM;
        }

        User user = User.builder()
            .userName(registrationForm.getUserName())
            .displayName(registrationForm.getFullName())
            .email(registrationForm.getEmail())
            .password(registrationForm.getPassword())
            .build();

        try {
            userService.registerNewUserAccount(user);
        } catch (AccountExistsException e) {
            if (e instanceof EmailExistsException) {
                model.addAttribute(EMAIL_EXISTS, true);
            } else {
                model.addAttribute(USER_EXISTS, true);
            }
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            return REGISTRATION_FORM;
        }

        return "redirect:/";
    }
}
