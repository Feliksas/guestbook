package service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import domain.auth.User;
import domain.message.GuestBookEntry;
import dto.GuestBookEntryDTO;
import dto.GuestBookEntryListDTO;
import dto.PageDTO;
import form.FeedbackForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.auth.UserRepository;
import repository.message.MessageRepository;

@Service
public class MessageService {
    private static final int ENTRIES_PER_PAGE = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public GuestBookEntryListDTO retrieveAllEntries(boolean threaded) {
        GuestBookEntryListDTO guestBookEntries = new GuestBookEntryListDTO();
        if (threaded) {
            List<GuestBookEntry> rootMessages = messageRepository.findRootMsgs();
            convertMessageListToDTO(rootMessages, guestBookEntries);

            for (GuestBookEntryDTO entry : guestBookEntries.getMessages()) {
                populateChildren(entry);
            }
        } else {
            List<GuestBookEntry> allMessages = messageRepository.findAll();
            convertMessageListToDTO(allMessages, guestBookEntries);
        }

        return guestBookEntries;
    }

    public Optional<GuestBookEntryDTO> retrieveMessage(int messageId, boolean withThread) {
        Optional<GuestBookEntry> messageOpt = messageRepository.findById(messageId);
        Optional<GuestBookEntryDTO> guestBookEntryOpt = Optional.empty();
        if (messageOpt.isPresent()) {
            guestBookEntryOpt = Optional.of(new GuestBookEntryDTO(messageOpt.get()));
            if (withThread) {
                populateChildren(guestBookEntryOpt.get());
            }
        }
        return guestBookEntryOpt;
    }

    private void convertMessageListToDTO(List<GuestBookEntry> messages, GuestBookEntryListDTO guestBookEntries) {
        for (GuestBookEntry message : messages) {
            guestBookEntries.getMessages().add(new GuestBookEntryDTO(message));
        }
    }

    public PageDTO retrieveAllEntriesByPage(int page) {
        PageDTO guestBookEntries = new PageDTO();

        PageRequest pageable = PageRequest.of(page - 1, ENTRIES_PER_PAGE, Sort.by("timeStamp").descending());
        Page<GuestBookEntry> guestBookPage = messageRepository.findRootMsgs(pageable);

        guestBookEntries.setTotalPages(guestBookPage.getTotalPages());

        for (GuestBookEntry entry : guestBookPage.getContent()) {
            populateChildren(guestBookEntries.convertAndAddEntity(entry));
        }

        return guestBookEntries;
    }

    private void populateChildren(GuestBookEntryDTO entryDto) {
        List<GuestBookEntry> childEntries = messageRepository.findAllByParentMsgId(entryDto.getId());
        for (GuestBookEntry childEntry : childEntries) {
            GuestBookEntryDTO childEntryDto = new GuestBookEntryDTO(childEntry);
            populateChildren(childEntryDto);
            entryDto.addReply(childEntryDto);
        }
    }

    @Transactional
    public void addEntry(FeedbackForm feedbackForm, Principal principal) {
        int posterId = -1;
        if (principal != null) {
            Optional<User> optLoggedInUser = userRepository.findByUserName(principal.getName());
            if (optLoggedInUser.isPresent()) {
                User loggedInUser = optLoggedInUser.get();
                feedbackForm.setName(loggedInUser.getDisplayName());
                feedbackForm.setEmail(loggedInUser.getEmail());
                posterId = loggedInUser.getId();
            }
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
    public void addReply(GuestBookEntryDTO newMessage, Principal principal) {
        Optional<User> optLoggedInUser = userRepository.findByUserName(principal.getName());

        if (optLoggedInUser.isPresent()) {
            User loggedInUser = optLoggedInUser.get();
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
    }

    @Transactional
    public void modifyEntry(GuestBookEntryDTO editedMessage, Principal principal) {
        Optional<User> optLoggedInUser = userRepository.findByUserName(principal.getName());
        Optional<GuestBookEntry> existingMessageOpt = messageRepository.findById(editedMessage.getId());

        if (existingMessageOpt.isPresent() && optLoggedInUser.isPresent()) {
            GuestBookEntry existingMessage = existingMessageOpt.get();
            User loggedInUser = optLoggedInUser.get();
            if (!existingMessage.getContent().equals(editedMessage.getContent()) &&
                canModifyMessage(loggedInUser, existingMessage.getPosterId())) {

                existingMessage.setContent(editedMessage.getContent());
                messageRepository.save(existingMessage);
            }
        }
    }

    @Transactional
    public void removeEntry(int postId, Principal principal) {
        Optional<User> optLoggedInUser = userRepository.findByUserName(principal.getName());
        Optional<GuestBookEntry> optMessage = messageRepository.findById(postId);

        if (optLoggedInUser.isPresent() && optMessage.isPresent()) {
            User loggedInUser = optLoggedInUser.get();
            GuestBookEntry message = optMessage.get();
            if (canModifyMessage(loggedInUser, message.getPosterId())) {
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

    private boolean canModifyMessage(User user, Integer posterId) {
        return user.isAdmin() || posterId.equals(user.getId());
    }
}