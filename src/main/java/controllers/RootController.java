package controllers;

import java.util.Date;
import model.GuestBookEntry;
import model.GuestBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path="/")
public class RootController {
    @Autowired
    private GuestBookRepository guestBookRepository;

    @PostMapping(path = "/add")
    public String addGuestBookEntry(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String content) {
        GuestBookEntry entry = new GuestBookEntry();

        entry.setName(name);
        entry.setEmail(email);
        entry.setContent(content);

        guestBookRepository.save(entry);

        return "guestbook";
    }

    @GetMapping(path="/")
    public String showGuestbook(Model model) {
        Iterable<GuestBookEntry> guestBookEntries = guestBookRepository.findAll();
        model.addAttribute("datetime", new Date());
        return "guestbook";
    }
}