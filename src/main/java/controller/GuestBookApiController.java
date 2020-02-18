package controller;

import dto.GuestBookEntryDTO;
import dto.GuestBookEntryListDTO;
import dto.UserDTO;
import dto.UserListDTO;
import exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.MessageService;
import service.UserDTOService;

@RestController
@RequestMapping("/api/v1")
public class GuestBookApiController {

    @Autowired
    UserDTOService userDTOService;

    @Autowired
    MessageService messageService;

    @GetMapping(value = {"/users", "/users.{format}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserListDTO getAllUsers() {
        return userDTOService.listUsers();
    }

    @GetMapping(value = {"/user/id/{id}", "/user/id/{id}.{format}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDTO> getUserById(@PathVariable(value = "id") int userId)
        throws ResourceNotFoundException {
        UserDTO user = userDTOService.retrieveUserById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found for id :: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @GetMapping(value = {"/user/name/{name}", "/user/name/{name}.{format}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<UserDTO> getUserByName(@PathVariable(value = "name") String userName)
        throws ResourceNotFoundException {
        UserDTO user = userDTOService.retrieveUserByName(userName)
            .orElseThrow(() -> new ResourceNotFoundException("User not found for username :: " + userName));
        return ResponseEntity.ok().body(user);
    }

    @GetMapping(value = {"/messages", "/messages.{format}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public GuestBookEntryListDTO getAllMessages(@RequestParam(required = false) boolean threaded) {
        return messageService.retrieveAllEntries(threaded);
    }

    @GetMapping(value = {"/message/id/{id}", "/message/id/{id}.{format}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<GuestBookEntryDTO> getMessageThreadById(
        @PathVariable(value = "id") int messageId,
        @RequestParam(required = false) boolean threaded)
        throws ResourceNotFoundException {

        GuestBookEntryDTO message = messageService.retrieveMessage(messageId, threaded)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found for id :: " + messageId));
        return ResponseEntity.ok().body(message);
    }
}
