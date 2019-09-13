package controllers;

import models.GuestBookEntry;
import models.GuestBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GuestBookController {
    @Autowired
    private GuestBookRepository guestBookRepository;

    @PostMapping(path = "/add")
    public String addGuestBookEntry(@ModelAttribute(name = "guestBookEntry") GuestBookEntry guestBookEntry) {
        guestBookRepository.save(guestBookEntry);
        return "guestbook";
    }

    @GetMapping(path="/")
    public String showGuestBook(Model model) {
        Iterable<GuestBookEntry> guestBookEntries = guestBookRepository.findAll();
        model.addAttribute("guestBookEntries", guestBookEntries);
        return "guestbook";
    }
}