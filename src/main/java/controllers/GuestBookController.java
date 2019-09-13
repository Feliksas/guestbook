package controllers;

import javax.validation.Valid;
import java.time.LocalDateTime;
import forms.FeedbackForm;
import models.GuestBookEntry;
import models.GuestBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GuestBookController implements WebMvcConfigurer {
    @Autowired
    private GuestBookRepository guestBookRepository;

    Logger log = LoggerFactory.getLogger(GuestBookController.class);

    @PostMapping(path = "/add")
    public String addGuestBookEntry(@Valid FeedbackForm feedbackForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.debug(bindingResult.getAllErrors().toString());
            redirectAttributes.addFlashAttribute("message", "<strong>Error! </strong> Invalid input! Please check your data.");
            redirectAttributes.addFlashAttribute("success", "false");
            return "redirect:/";
        }
        GuestBookEntry guestBookEntry = new GuestBookEntry();

        guestBookEntry.setTimeStamp(LocalDateTime.now());
        guestBookEntry.setName(feedbackForm.getName());
        guestBookEntry.setEmail(feedbackForm.getEmail());
        guestBookEntry.setContent(feedbackForm.getFeedback());

        guestBookRepository.save(guestBookEntry);

        redirectAttributes.addFlashAttribute("message", "<strong>Success! </strong> Your feedback has been recorded!");
        redirectAttributes.addFlashAttribute("success", "true");
        return "redirect:/";
    }

    @GetMapping(path="/")
    public String showGuestBook(Model model, @ModelAttribute(name="message") String message,
                                @ModelAttribute(name="success") String success, FeedbackForm feedbackForm) {
        Iterable<GuestBookEntry> guestBookEntries = guestBookRepository.findAll(Sort.by("timeStamp").descending());
        //model.addAttribute("feedbackForm", feedbackForm);
        model.addAttribute("guestBookEntries", guestBookEntries);
        model.addAttribute("message",""+message);
        model.addAttribute("success", success);
        return "guestbook";
    }
}