package service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import forms.FeedbackForm;
import forms.PageDto;
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
public class MessageService {
    private static int ENTRIES_PER_PAGE = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public PageDto retrieveAllEntriesByPage(int page) {
        PageDto guestBookEntries = new PageDto();

        PageRequest pageable = PageRequest.of(page - 1, ENTRIES_PER_PAGE, Sort.by("timeStamp").descending());
        Page<GuestBookEntry> guestBookPage = messageRepository.findRootMsgs(pageable);

        guestBookEntries.setTotalPages(guestBookPage.getTotalPages());

        for (GuestBookEntry entry : guestBookPage.getContent()) {
            populateChildren(guestBookEntries.convertAndAddEntity(entry));
        }

        return guestBookEntries;
    }

    private void populateChildren(GuestBookEntryDto entryDto) {
        List<GuestBookEntry> childEntries = messageRepository.findAllByParentMsgId(entryDto.getId());
        for (GuestBookEntry childEntry : childEntries) {
            GuestBookEntryDto childEntryDto = new GuestBookEntryDto(childEntry);
            populateChildren(childEntryDto);
            entryDto.addEntry(childEntryDto);
        }
    }

    @Transactional
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
    public void addReply(GuestBookEntryDto newMessage, Principal principal) {
        User loggedInUser = userRepository.findByUserName(principal.getName());

        if (canModifyMessage(loggedInUser, newMessage.getPosterId())) {
            GuestBookEntry reply = GuestBookEntry.builder()
                .parentMsgId(newMessage.getParentMsgId())
                .content(newMessage.getContent())
                .email(loggedInUser.getEmail())
                .name(loggedInUser.getDisplayName())
                .posterId(loggedInUser.getId())
                .timeStamp(LocalDateTime.now())
                .build();
            messageRepository.save(reply);
        }
    }

    @Transactional
    public void modifyEntry(GuestBookEntryDto editedMessage, Principal principal) {
        User loggedInUser = userRepository.findByUserName(principal.getName());

        Optional<GuestBookEntry> existingMessageOpt = messageRepository.findById(editedMessage.getId());

        if (existingMessageOpt.isPresent()) {
            GuestBookEntry existingMessage = existingMessageOpt.get();
            if (!existingMessage.getContent().equals(editedMessage.getContent()) &&
                canModifyMessage(loggedInUser, existingMessage.getPosterId())) {

                existingMessage.setContent(editedMessage.getContent());
                messageRepository.save(existingMessage);
            }
        }
    }

    @Transactional
    public void removeEntry(int postId, Principal principal) {
        User loggedInUser = userRepository.findByUserName(principal.getName());
        GuestBookEntry message = messageRepository.findById(postId);

        if (message != null && canModifyMessage(loggedInUser, message.getPosterId())) {
            if (messageRepository.countChildren(postId) > 0) {
                // Leave message thread intact, just zero parent message
                message.setContent(null);
                messageRepository.save(message);
            } else {
                messageRepository.delete(message);
            }
        }
    }

    private boolean canModifyMessage(User user, Integer posterId) {
        return user.isAdmin() || posterId.equals(user.getId());
    }
}