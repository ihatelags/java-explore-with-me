package ru.practicum.explorewithme.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.request.Dto;
import ru.practicum.explorewithme.service.request.RequestService;
import ru.practicum.explorewithme.util.mapper.RequestMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class RequestControllerPrivate {

    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    public Dto.ParticipantRequestDto createRequest(@PathVariable Long userId,
                                                   @RequestParam(required = false) Long eventId) {
        log.info("Получен POST запрос к эндпоинту /users/{}/requests?eventId={}", userId, eventId);
        return RequestMapper.toParticipantRequestDto(requestService.createRequest(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{reqId}/cancel")
    public Dto.ParticipantRequestDto cancelRequest(@PathVariable() Long userId,
                                                   @PathVariable() Long reqId) {
        log.info("Получен PATCH запрос к эндпоинту /users/{}/requests/{}/cancel", userId, reqId);
        return RequestMapper.toParticipantRequestDto(requestService.cancelRequest(userId, reqId));
    }

    @GetMapping("/{userId}/requests")
    public Collection<Dto.ParticipantRequestDto> getAllRequestsByUser(@PathVariable() Long userId) {
        log.info("Получен GET запрос к эндпоинту /users/{}/requests", userId);
        return RequestMapper.toParticipantRequestDtoCollection(requestService.getAllRequestsByUser(userId));
    }
}
