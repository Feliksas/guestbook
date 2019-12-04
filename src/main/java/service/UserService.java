package service;


import domain.auth.Role;
import domain.auth.User;
import exception.AccountExistsException;
import exception.EmailExistsException;
import exception.UserNameExistsException;
import form.RegistrationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.auth.UserRepository;

@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmailWithEagerRoles(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipal(user);
    }

    public void registerNewUserAccount(RegistrationForm registrationForm) throws AccountExistsException {
        User user = User.builder()
            .userName(registrationForm.getUserName())
            .displayName(registrationForm.getFullName())
            .email(registrationForm.getEmail())
            .password(registrationForm.getPassword())
            .build();

        String email = user.getEmail();
        String userName = user.getUserName();

        if (emailExists(email)) {
            throw new EmailExistsException(String.format("User with email %s already exists", email));
        } else if (userNameExists(userName)) {
            throw new UserNameExistsException(String.format("User with username %s already exists", userName));
        } else {
            user.getRoles().add(Role.ROLE_USER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);

            userRepository.save(user);
        }
    }

    public boolean isNameOrEmailTaken(String name, String email) {
        User existingUser = userRepository.findByDisplayNameOrEmail(name, email);
        return existingUser != null;
    }

    private boolean emailExists(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    private boolean userNameExists(String userName) {
        User user = userRepository.findByUserName(userName);
        return user != null;
    }
}

