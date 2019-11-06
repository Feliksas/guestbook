package models.auth;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.CascadeType;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "USERNAME", unique = true)
    @Length(min = 3)
    @NotEmpty(message = "*Please provide your username")
    private String userName;

    @Column(name = "DISPLAYNAME")
    @Length(min = 3)
    @NotEmpty(message = "*Please provide your display name")
    private String displayName;

    @Column(name = "PASSWORD")
    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    private String password;

    @Column(name = "EMAIL", unique = true, nullable = false)
    @Email
    @Length(min = 6)
    private String email;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLES",
               joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
               inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    @Builder.Default
    private Set<Role> roles = new LinkedHashSet<>();

    public boolean isAdmin() {
        return this.roles.contains(Role.ROLE_ADMIN);
    }
}
