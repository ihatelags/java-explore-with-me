package ru.practicum.explorewithme.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.BadRequestException;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.model.user.dto.NewUserRequestDto;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.util.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(@NotNull NewUserRequestDto newUserRequestDto) {
        User user = UserMapper.toUser(newUserRequestDto);
        validateUser(user);
        User userFromDataBase = userRepository.save(user);
        log.info("Запрос на создание пользователя с id - {}", user.getId());
        return userFromDataBase;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ид " + userId));
        log.info("Запрос на получение пользователя с id - {}", userId);
        return user;
    }

    @Override
    public Collection<User> getAllUsers(@NotNull List<Long> ids, PageRequest pageRequest) {
        if (ids.isEmpty()) {
            return userRepository.getAllUsersByPage(pageRequest).stream()
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllById(ids);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        getUserById(userId);
        log.info("Запрос на удаление пользователя с id - {}", userId);
        userRepository.deleteById(userId);
    }

    public void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Передан null вместо пользователя.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new BadRequestException("Имя пользователя не может быть пустым.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new BadRequestException("Имейл не должен быть пустым и должен содержать символ @.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Пользователь с имейлом " + user.getEmail()+ " уже существует.");
        }
    }
}
