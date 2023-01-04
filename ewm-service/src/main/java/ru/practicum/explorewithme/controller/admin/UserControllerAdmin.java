package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.user.dto.NewUserRequestDto;
import ru.practicum.explorewithme.model.user.dto.UserDto;
import ru.practicum.explorewithme.service.user.UserService;
import ru.practicum.explorewithme.util.ValidationPageParam;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserControllerAdmin {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /admin/users?ids={}from={}size={}", ids, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return userService.getAllUsers(ids, pageRequest);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен GET запрос к эндпоинту /admin/users/{}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody NewUserRequestDto newUserRequestDto) {
        log.info("Получен POST запрос к эндпоинту /admin/users");
        return userService.createUser(newUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен DELETE запрос к эндпоинту /admin/users/{}", userId);
        userService.deleteUser(userId);
    }

    public PageRequest findPageRequest(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }

    private void validatePage(Integer from, Integer size) {
        ValidationPageParam validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}