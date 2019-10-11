package models.auth;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(exclude = {"users"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ROLE")
public class Role {
    public static final Role ROLE_ADMIN = new Role(0,"ADMIN", new HashSet<>());
    public static final Role ROLE_USER = new Role(1,"USER", new HashSet<>());
    private static final List<Role> allRoles = Arrays.asList(ROLE_ADMIN, ROLE_USER);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "ROLE", unique = true)
    private String role;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    public static Role getRoleByName(String name) {
        return allRoles.stream().filter(role -> role.getRole().equals(name)).findAny().orElse(null);
    }

    public static List<Role> getAllRoles() {
        return allRoles;
    }
}
