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
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SmartValidator validator;

    @PostMapping(path = "/add")
    public String addGuestBookEntry(FeedbackForm feedbackForm,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal) {
        int posterId = -1;

        if (principal != null) {
            User loggedInUser = userRepository.findByUserName(principal.getName());
            feedbackForm.setName(loggedInUser.getDisplayName());
            feedbackForm.setEmail(loggedInUser.getEmail());
            posterId = loggedInUser.getId();
        }

        ValidationUtils.invokeValidator(validator, feedbackForm, bindingResult);
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, showGuestBookEntries(1));
            return GUESTBOOK_VIEW;
        }

        User existingUser = userRepository.findByDisplayNameOrEmail(feedbackForm.getName(), feedbackForm.getEmail());

        if (existingUser != null && principal == null) {
            bindingResult.addError(new FieldError("feedbackForm", "name", MESSAGE_USEREXISTS));
            bindingResult.addError(new FieldError("feedbackForm", "email", MESSAGE_USEREXISTS));
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, showGuestBookEntries(1));
            return GUESTBOOK_VIEW;
        }

        GuestBookEntry guestBookEntry = GuestBookEntry.builder()
            .timeStamp(LocalDateTime.now())
            .name(feedbackForm.getName())
            .email(feedbackForm.getEmail())
            .content(feedbackForm.getFeedback())
            .posterId(posterId)
            .build();

        redirectAttributes.addFlashAttribute(MESSAGE_ATTR, MESSAGE_SUCCESS);
        messageRepository.save(guestBookEntry);

        return "redirect:/";
    }

    @PostMapping(path = "/edit")
    @Transactional
    public String editGuestBookEntry(@Valid GuestBookEntriesListDto guestBookEntriesListDto,
                                     BindingResult bindingResult,
                                     Principal principal) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
        }

        User loggedInUser = userRepository.findByUserName(principal.getName());

        GuestBookEntryDto modifiedMessage = guestBookEntriesListDto.getEntries().get(0);
        GuestBookEntry existingMessage = messageRepository.findById(modifiedMessage.getId());

        if (existingMessage != null) {
            if (!existingMessage.getContent().equals(modifiedMessage.getContent()) &&
                (existingMessage.getPosterId().equals(loggedInUser.getId()) ||
                 loggedInUser.isAdmin())) {

                existingMessage.setContent(modifiedMessage.getContent());
                messageRepository.save(existingMessage);
            }
        } else if(modifiedMessage.getParentMsgId() != null) {
            GuestBookEntry reply = GuestBookEntry.builder()
                .parentMsgId(modifiedMessage.getParentMsgId())
                .content(modifiedMessage.getContent())
                .email(loggedInUser.getEmail())
                .name(loggedInUser.getDisplayName())
                .posterId(loggedInUser.getId())
                .timeStamp(LocalDateTime.now())
                .build();
            messageRepository.save(reply);
        }

        return "redirect:/";
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void deleteGuestBookEntry(@RequestParam(name="id") int postId,
                                     Principal principal) {
        User loggedInUser = userRepository.findByUserName(principal.getName());
        GuestBookEntry message = messageRepository.findById(postId);

        if (message != null) {
            if (message.getPosterId().equals(loggedInUser.getId()) ||
                loggedInUser.isAdmin()) {
                if (messageRepository.countChildren(postId) > 0) {
                    message.setContent(null);
                    messageRepository.save(message);
                } else {
                    messageRepository.delete(message);
                }
            }
        }
    }

    @ModelAttribute(name = "feedbackForm")
    public FeedbackForm showFeedbackForm() {
        return new FeedbackForm();
    }

    private void populateChildren(GuestBookEntryDto entryDto) {
        List<GuestBookEntry> childEntries = messageRepository.findAllByParentMsgId(entryDto.getId());
        for (GuestBookEntry childEntry : childEntries) {
            GuestBookEntryDto childEntryDto = new GuestBookEntryDto(childEntry);
            populateChildren(childEntryDto);
            entryDto.addEntry(childEntryDto);
        }
    }

    private GuestBookEntriesListDto showGuestBookEntries(int page) {
        GuestBookEntriesListDto guestBookEntries = new GuestBookEntriesListDto();

        PageRequest pageable = PageRequest.of(page - 1, 3, Sort.by("timeStamp").descending());
        Page<GuestBookEntry> guestBookPage = messageRepository.findRootMsgs(pageable);
        int totalPages = guestBookPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            guestBookEntries.setPages(pageNumbers);
        }
        for (GuestBookEntry entry : guestBookPage.getContent()) {
            guestBookEntries.addEntry(entry);
        }

        for (GuestBookEntryDto entryDto : guestBookEntries.getEntries()) {
            populateChildren(entryDto);
        }

        return guestBookEntries;
    }

    @GetMapping(path = "/")
    public String showGuestBook(Model model) {
        model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, showGuestBookEntries(1));
        return GUESTBOOK_VIEW;
    }

    @GetMapping(path = "/reviews/{page}")
    public String showGuestBookByPage(@PathVariable("page") int page, Model model) {
        model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, showGuestBookEntries(page));
        return GUESTBOOK_VIEW;
    }
}