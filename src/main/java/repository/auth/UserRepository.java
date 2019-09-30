package repository.auth;


import java.util.List;
import models.auth.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.userName = :username OR u.email = :email")
    User findByUserNameOrEmailWithEagerRoles(String username, String email);

    User findByUserNameOrEmail(String username, String email);

    User findByUserName(String username);

    User findByEmail(String email);

    @Override
    List<User> findAll();

    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllWithRoles(Sort sort);
}