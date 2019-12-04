package repository.auth;


import java.util.List;
import domain.auth.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.userName = :token OR u.email = :token")
    User findByUserNameOrEmailWithEagerRoles(String token);

    User findByUserName(String username);

    User findByEmail(String email);

    User findByDisplayNameOrEmail(String displayName, String email);

    @Override
    List<User> findAll();

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles(Sort sort);
}