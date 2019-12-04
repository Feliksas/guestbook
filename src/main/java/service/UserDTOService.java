package service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import domain.auth.User;
import dto.UserDTO;
import dto.UserListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import repository.auth.UserRepository;

@Service
public class UserDTOService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDTOToEntityService userDTOToEntityService;

    public UserListDTO listUsers() {
        UserListDTO dtoUsers = new UserListDTO();
        List<User> dbUsers = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));
        for (User user : dbUsers) {
            dtoUsers.addUser(user);
        }
        return dtoUsers;
    }

    public void persistDTOList(List<UserDTO> dtos) {
        List<User> entities = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));

        List<UserDTO> dtosToDelete = findDTOsToDelete(dtos);
        List<User> entitiesToDelete = findEntitiesToDelete(dtosToDelete, entities);

        dtos.removeAll(dtosToDelete);
        entities.removeAll(entitiesToDelete);

        merge(dtos, entities);

        userRepository.saveAll(entities);
        userRepository.deleteAll(entitiesToDelete);
    }

    private List<UserDTO> findDTOsToDelete(List<UserDTO> dtos) {
        return dtos.stream()
            .filter(UserDTO::isDelete)
            .collect(toList());
    }

    private List<User> findEntitiesToDelete(List<UserDTO> dtosToDelete, List<User> entities) {
        return dtosToDelete.stream()
            .map(dto -> entities.stream()
                .filter(entity -> entity.getId() == dto.getId())
                .findFirst()
                .orElseThrow(RuntimeException::new))
            .collect(Collectors.toList());
    }

    private void merge(List<UserDTO> dtos, List<User> entities) {
        for (UserDTO dto : dtos) {
            Optional<User> entityOpt = entities.stream().filter(e -> e.getId() == dto.getId()).findFirst();
            if (entityOpt.isPresent()) {
                User entity = entityOpt.get();
                userDTOToEntityService.convert(dto, entity);
            } else {
                User user = userDTOToEntityService.convert(dto);
                entities.add(user);
            }
        }
    }

}
