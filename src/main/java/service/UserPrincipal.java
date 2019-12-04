package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import domain.auth.Role;
import domain.auth.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@SuppressWarnings("unused")
public class UserPrincipal implements UserDetails {
    private final User user;

    UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    // Used in Thymeleaf template
    public String getDisplayName() {
        return user.getDisplayName();
    }

    public int getId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    // Used in Thymeleaf template
    public boolean isAdmin() {
        return user.isAdmin();
    }
}
