package controllers;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import forms.FeedbackForm;
import forms.GuestBookEntriesListDto;
import forms.GuestBookEntryDto;
import lombok.extern.slf4j.Slf4j;
import models.auth.User;
import models.messages.GuestBookEntry;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import repository.auth.UserRepository;
import repository.messages.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.GuestBookEntryService;

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
    private UserRepository userRepository;

    @Autowired
    GuestBookEntryService guestBookEntryService;

    @Autowired
    SmartValidator validator;

    @GetMapping(path = "/")
    public String showGuestBook(Model model) {
        model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, guestBookEntryService.retrieveAllEntriesByPage(1));
        return GUESTBOOK_VIEW;
    }

    @GetMapping(path = "/reviews/{page}")
    public String showGuestBookByPage(@PathVariable("page") int page, Model model) {
        model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, guestBookEntryService.retrieveAllEntriesByPage(page));
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
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, guestBookEntryService.retrieveAllEntriesByPage(1));
            return GUESTBOOK_VIEW;
        }

        // Prevent anons from impersonating registered users
        User existingUser = userRepository.findByDisplayNameOrEmail(feedbackForm.getName(), feedbackForm.getEmail());
        if (existingUser != null && principal == null) {
            bindingResult.addError(new FieldError("feedbackForm", "name", MESSAGE_USEREXISTS));
            bindingResult.addError(new FieldError("feedbackForm", "email", MESSAGE_USEREXISTS));
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, guestBookEntryService.retrieveAllEntriesByPage(1));
            return GUESTBOOK_VIEW;
        }

        guestBookEntryService.addEntry(feedbackForm, principal);

        redirectAttributes.addFlashAttribute(MESSAGE_ATTR, MESSAGE_SUCCESS);

        return "redirect:/";
    }

    @PostMapping(path = "/edit")
    public String editGuestBookEntry(@Valid GuestBookEntriesListDto guestBookEntriesListDto,
                                     BindingResult bindingResult,
                                     Principal principal) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return GUESTBOOK_VIEW;
        }

        guestBookEntryService.modifyEntry(guestBookEntriesListDto, principal);

        return "redirect:/";
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGuestBookEntry(@RequestParam(name="id") int postId,
                                     Principal principal) {
        guestBookEntryService.removeEntry(postId, principal);
    }

    @ModelAttribute(name = "feedbackForm")
    public FeedbackForm showFeedbackForm() {
        return new FeedbackForm();
    }
}