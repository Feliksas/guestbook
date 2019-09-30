package controllers;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import forms.FeedbackForm;
import lombok.extern.slf4j.Slf4j;
import models.messages.GuestBookEntry;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class GuestBookController {
    private static final String GUESTBOOK_VIEW = "guestbook";
    private static final String GUESTBOOK_ENTRIES_BLOCK = "guestBookEntriesBlock";

    private static final String MESSAGE_FAIL = "<strong>Error!</strong> Please check your input!";
    private static final String MESSAGE_SUCCESS = "<strong>Success!</strong> Your feedback has been saved!";
    private static final String MESSAGE_ATTR = "submitResultMessage";

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping(path = "/add")
    public String addGuestBookEntry(@Valid FeedbackForm feedbackForm,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute(MESSAGE_ATTR, MESSAGE_FAIL);
            model.addAttribute(GUESTBOOK_ENTRIES_BLOCK, showGuestBookEntries(1));
            return GUESTBOOK_VIEW;
        }
        GuestBookEntry guestBookEntry = GuestBookEntry.builder()
            .timeStamp(LocalDateTime.now())
            .name(feedbackForm.getName())
            .email(feedbackForm.getEmail())
            .content(feedbackForm.getFeedback()).build();

        redirectAttributes.addFlashAttribute(MESSAGE_ATTR, MESSAGE_SUCCESS);
        messageRepository.save(guestBookEntry);

        return "redirect:/";
    }

    @ModelAttribute(name = "feedbackForm")
    public FeedbackForm showFeedbackForm() {
        return new FeedbackForm();
    }

    private HashMap<String, Object> showGuestBookEntries(int page) {
        HashMap<String, Object> guestBookEntriesBlock = new HashMap<>();

        PageRequest pageable = PageRequest.of(page - 1, 3, Sort.by("timeStamp").descending());
        Page<GuestBookEntry> guestBookPage = messageRepository.findAll(pageable);
        int totalPages = guestBookPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            guestBookEntriesBlock.put("pageNumbers", pageNumbers);
        }
        guestBookEntriesBlock.put("guestBookEntries", guestBookPage.getContent());

        return guestBookEntriesBlock;
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