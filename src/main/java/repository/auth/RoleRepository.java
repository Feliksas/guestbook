package repository.auth;

import java.util.List;
import domain.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Override
    List<Role> findAll();
}
