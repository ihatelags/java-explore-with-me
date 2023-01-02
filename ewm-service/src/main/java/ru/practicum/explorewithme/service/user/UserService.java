package ru.practicum.explorewithme.service.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.model.user.dto.NewUserRequestDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User createUser(NewUserRequestDto newUserRequestDto);

    User getUserById(Long userId);

    Collection<User> getAllUsers(List<Long> ids, PageRequest pageRequest);

    void deleteUser(Long userId);
}
