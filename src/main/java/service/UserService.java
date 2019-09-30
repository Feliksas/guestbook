package service;


import java.util.Collections;
import java.util.HashSet;
import exceptions.AccountExistsException;
import exceptions.EmailExistsException;
import exceptions.UserNameExistsException;
import lombok.extern.slf4j.Slf4j;
import models.auth.Role;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.auth.RoleRepository;
import repository.auth.UserRepository;

@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmailWithEagerRoles(username, username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipal(user);
    }

    public User registerNewUserAccount(User user) throws AccountExistsException {
        String email = user.getEmail();
        String userName = user.getUserName();

        SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();

        if (emailExists(email)) {
            throw new EmailExistsException(String.format("User with email %s already exists", email));
        } else if (userNameExists(userName)) {
            throw new UserNameExistsException(String.format("User with username %s already exists", userName));
        } else {
            Role role = roleRepository.findByRole(Role.ROLE_USER);
            if (role == null) {
                log.info(String.format("Role \"%s\" does not exist, creating", Role.ROLE_USER));
                role = Role.builder().role(Role.ROLE_USER).build();
            }

            user.setRoles(new HashSet<>(Collections.singletonList(role)));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);

            return userRepository.save(user);
        }
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

