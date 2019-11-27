package service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import forms.UserDto;
import models.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import repository.auth.UserRepository;

@Service
public class UserDTOService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDtoToEntityService userDtoToEntityService;

    public void persistDtoList(List<UserDto> dtos) {
        List<User> entities = userRepository.findAllWithRoles(Sort.by(Sort.Direction.ASC, "userName"));

        List<UserDto> dtosToDelete = findDtosToDelete(dtos);
        List<User> entitiesToDelete = findEntitiesToDelete(dtosToDelete, entities);

        dtos.removeAll(dtosToDelete);
        entities.removeAll(entitiesToDelete);

        merge(dtos, entities);

        userRepository.saveAll(entities);
        userRepository.deleteAll(entitiesToDelete);
    }

    private List<UserDto> findDtosToDelete(List<UserDto> dtos) {
        return dtos.stream()
            .filter(UserDto::isDelete)
            .collect(toList());
    }

    private List<User> findEntitiesToDelete(List<UserDto> dtosToDelete, List<User> entities) {
        return dtosToDelete.stream()
                .map(dto -> entities.stream()
                    .filter(entity -> entity.getId() == dto.getId())
                    .findFirst()
                    .orElseThrow(RuntimeException::new))
                .collect(Collectors.toList());
    }

    private void merge(List<UserDto> dtos, List<User> entities) {
        for (UserDto dto : dtos) {
            Optional<User> entityOpt = entities.stream().filter(e -> e.getId() == dto.getId()).findFirst();
            if (entityOpt.isPresent()) {
                User entity = entityOpt.get();
                userDtoToEntityService.convert(dto, entity);
            } else {
                User user = userDtoToEntityService.convert(dto);
                entities.add(user);
            }
        }
    }

}
