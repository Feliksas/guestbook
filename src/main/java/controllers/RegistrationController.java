package controllers;

import javax.validation.Valid;
import exceptions.AccountExistsException;
import exceptions.EmailExistsException;
import exceptions.UserNameExistsException;
import forms.RegistrationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import service.UserService;

@Controller
@Slf4j
public class RegistrationController {
    private static final String REGISTRATION_VIEW = "register";

    private static final String USER_EXISTS = "userExists";
    private static final String EMAIL_EXISTS = "emailExists";

    private static final String MESSAGE_FAIL = "<strong>Error!</strong> Please check your input!";
    private static final String MESSAGE_ATTR = "submitResultMessage";

    @Autowired
    private UserService userService;

    @ModelAttribute("registrationForm")
    public RegistrationForm showRegistrationForm() { return new RegistrationForm(); }

    @GetMapping(path="/register")
    public String newUserRegistration() {
        return REGISTRATION_VIEW;
    }

    @PostMapping(path="/register")
    public String doRegister(@Valid RegistrationForm registrationForm,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            return REGISTRATION_VIEW;
        }

        try {
            userService.registerNewUserAccount(registrationForm);
            return "redirect:/";
        } catch (EmailExistsException e) {
                model.addAttribute(EMAIL_EXISTS, true);
        } catch (UserNameExistsException e) {
                model.addAttribute(USER_EXISTS, true);
        } catch (AccountExistsException e) {
            log.debug("Unknown AccountExistsException occurred: ", e);
        }
        model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
        return REGISTRATION_VIEW;
    }
}
