package service;


import java.util.Arrays;
import java.util.HashSet;
import exceptions.AccountExistsException;
import exceptions.EmailExistsException;
import exceptions.UserNameExistsException;
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
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmail(username, username);
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
            user.setRoles(new HashSet<>(Arrays.asList(role)));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            return userRepository.save(user);
        }
    }

    private boolean emailExists(String email) {
        User user = userRepository.findByEmail(email);
        if ( user != null) {
            return true;
        }
        return false;
    }

    private boolean userNameExists(String userName) {
        User user = userRepository.findByUserName(userName);
        if ( user != null) {
            return true;
        }
        return false;
    }
}
