package ru.practicum.explorewithme.service.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explorewithme.model.user.dto.NewUserRequestDto;
import ru.practicum.explorewithme.model.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserRequestDto);

    UserDto getUserById(Long userId);

    Collection<UserDto> getAllUsers(List<Long> ids, PageRequest pageRequest);

    void deleteUser(Long userId);
}
