package repository.auth;


import java.util.List;
import java.util.Optional;
import domain.auth.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.userName = :token OR u.email = :token")
    Optional<User> findByUserNameOrEmailWithEagerRoles(String token);

    Optional<User> findByUserName(String username);

    User findByEmail(String email);

    User findByDisplayNameOrEmail(String displayName, String email);

    @Override
    List<User> findAll();

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles(Sort sort);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(int id);
}