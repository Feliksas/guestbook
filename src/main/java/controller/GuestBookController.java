package controller;

import javax.validation.Valid;
import java.security.Principal;
import dto.GuestBookEntryDTO;
import form.FeedbackForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.MessageService;
import service.UserService;

@Controller
@Slf4j
public class GuestBookController {
    private static final String GUESTBOOK_VIEW = "guestbook";
    private static final String GUESTBOOK_ENTRIES_BLOCK = "guestBookEntries";

    private static final String MESSAGE_FAIL = "<strong>Error!</strong> Please check your input!";
    private static final String MESSAGE_USEREXISTS = "This name or e-mail is already taken!";
    private static final String MESSAGE_SUCCESS = "<strong>Success!</strong> Your feedback has been saved!";
    private static final String MESSAGE_ATTR = "submitResultMessage";

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    SmartValidator validator;

    @GetMapping(path = "/")
    public String showGuestBook(Model model) {
        return showGuestBookByPage(1, model);
    }

    @GetMapping(path = "/reviews/{page}")
    public String showGuestBookByPage(@PathVariable("page") int page, Model model) {
        model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, messageService.retrieveAllEntriesByPage(page));
        return GUESTBOOK_VIEW;
    }

    @PostMapping(path = "/add")
    public String addGuestBookEntry(FeedbackForm feedbackForm,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal) {

        ValidationUtils.invokeValidator(validator, feedbackForm, bindingResult);
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, messageService.retrieveAllEntriesByPage(1));
            return GUESTBOOK_VIEW;
        }

        if (principal == null && userService.isNameOrEmailTaken(feedbackForm.getName(), feedbackForm.getEmail())) {
            bindingResult.addError(new FieldError("feedbackForm", "name", MESSAGE_USEREXISTS));
            bindingResult.addError(new FieldError("feedbackForm", "email", MESSAGE_USEREXISTS));
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, messageService.retrieveAllEntriesByPage(1));
            return GUESTBOOK_VIEW;
        }

        messageService.addEntry(feedbackForm, principal);

        redirectAttributes.addFlashAttribute(MESSAGE_ATTR, MESSAGE_SUCCESS);

        return "redirect:/";
    }

    @PostMapping(path = "/edit")
    public String editGuestBookEntry(@Valid GuestBookEntryDTO guestBookEntryDto,
                                     BindingResult bindingResult,
                                     Principal principal) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return GUESTBOOK_VIEW;
        }
        if (guestBookEntryDto.getParentMsgId() != null) {
            messageService.addReply(guestBookEntryDto, principal);
        } else {
            messageService.modifyEntry(guestBookEntryDto, principal);
        }
        return "redirect:/";
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGuestBookEntry(@RequestParam(name = "id") int postId,
                                     Principal principal) {
        messageService.removeEntry(postId, principal);
    }

    @ModelAttribute(name = "feedbackForm")
    public FeedbackForm showFeedbackForm() {
        return new FeedbackForm();
    }
}