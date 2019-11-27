package service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import forms.FeedbackForm;
import forms.GuestBookEntriesListDto;
import forms.GuestBookEntryDto;
import models.auth.User;
import models.messages.GuestBookEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.auth.UserRepository;
import repository.messages.MessageRepository;

@Service
public class GuestBookEntryService {
    private static int ENTRIES_PER_PAGE = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public GuestBookEntriesListDto retrieveAllEntriesByPage(int page) {
        GuestBookEntriesListDto guestBookEntries = new GuestBookEntriesListDto();

        PageRequest pageable = PageRequest.of(page - 1, ENTRIES_PER_PAGE, Sort.by("timeStamp").descending());
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

    public void addEntry(FeedbackForm feedbackForm, Principal principal) {
        int posterId = -1;
        if (principal != null) {
            User loggedInUser = userRepository.findByUserName(principal.getName());
            feedbackForm.setName(loggedInUser.getDisplayName());
            feedbackForm.setEmail(loggedInUser.getEmail());
            posterId = loggedInUser.getId();
        }

        GuestBookEntry guestBookEntry = GuestBookEntry.builder()
            .timeStamp(LocalDateTime.now())
            .name(feedbackForm.getName())
            .email(feedbackForm.getEmail())
            .content(feedbackForm.getFeedback())
            .posterId(posterId)
            .build();

        messageRepository.save(guestBookEntry);
    }

    @Transactional
    public void modifyEntry(GuestBookEntriesListDto guestBookEntriesListDto, Principal principal) {
        User loggedInUser = userRepository.findByUserName(principal.getName());

        GuestBookEntryDto modifiedMessage = guestBookEntriesListDto.getEntries().get(0);
        GuestBookEntry existingMessage = messageRepository.findById(modifiedMessage.getId());

        if (existingMessage != null) {
            if (!existingMessage.getContent().equals(modifiedMessage.getContent()) &&
                canModifyMessage(loggedInUser, existingMessage)) {

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
    }

    @Transactional
    public void removeEntry(int postId, Principal principal) {
        User loggedInUser = userRepository.findByUserName(principal.getName());
        GuestBookEntry message = messageRepository.findById(postId);

        if (message != null) {
            if (canModifyMessage(loggedInUser, message)) {
                if (messageRepository.countChildren(postId) > 0) {
                    // Leave message thread intact, just zero parent message
                    message.setContent(null);
                    messageRepository.save(message);
                } else {
                    messageRepository.delete(message);
                }
            }
        }
    }

    private void populateChildren(GuestBookEntryDto entryDto) {
        List<GuestBookEntry> childEntries = messageRepository.findAllByParentMsgId(entryDto.getId());
        for (GuestBookEntry childEntry : childEntries) {
            GuestBookEntryDto childEntryDto = new GuestBookEntryDto(childEntry);
            populateChildren(childEntryDto);
            entryDto.addEntry(childEntryDto);
        }
    }

    private boolean canModifyMessage(User user, GuestBookEntry message) {
        return user.isAdmin() || message.getPosterId().equals(user.getId());
    }
}