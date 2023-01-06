package ru.practicum.explorewithme.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.event.dto.EventFullDto;
import ru.practicum.explorewithme.model.event.dto.EventShortDto;
import ru.practicum.explorewithme.model.event.dto.NewEventDto;
import ru.practicum.explorewithme.model.event.dto.UpdateEventRequest;
import ru.practicum.explorewithme.model.request.dto.RequestDto;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.service.request.RequestService;
import ru.practicum.explorewithme.util.ValidationPageParam;
import ru.practicum.explorewithme.util.mapper.EventMapper;
import ru.practicum.explorewithme.util.mapper.RequestMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class EventControllerPrivate {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody NewEventDto newEventDto) {
        log.info("Получен POST запрос к эндпоинту /users/{}/events", userId);
        return EventMapper.toEventFullDto(eventService.createEvent(userId, newEventDto));
    }

    @GetMapping("/{userId}/events")
    public Collection<EventShortDto> getAllEventsByUser(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /users/{}/events?from={}size={}", userId, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return eventService.getAllEventsByUser(userId, pageRequest);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Получен PATCH запрос к эндпоинту /users/{}/events", userId);
        return EventMapper.toEventFullDto(eventService.updateEvent(userId, updateEventRequest));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("Получен PATCH запрос к эндпоинту /users/{}/events/{}", userId, eventId);
        return EventMapper.toEventFullDto(eventService.cancelEvent(userId, eventId));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Получен GET запрос к эндпоинту /users/{}/events/{}", userId, eventId);
        return EventMapper.toEventFullDto(eventService.getEventById(eventId));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public Collection<RequestDto> getAllRequestsByEvent(@PathVariable() Long userId,
                                                                   @PathVariable() Long eventId) {
        log.info("Получен GET запрос к эндпоинту /users/{}/events/{}/requests", userId, eventId);
        return RequestMapper.toRequestDtoCollection(requestService.getAllRequestsByEvent(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable() Long userId,
                                                @PathVariable() Long eventId,
                                                @PathVariable() Long reqId) {
        log.info("Получен PATCH запрос к эндпоинту /users/{}/events/{}/requests/{}/confirm",
                userId, eventId, reqId);
        return RequestMapper.toRequestDto(requestService.confirmRequest(userId, eventId, reqId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable() Long userId,
                                               @PathVariable() Long eventId,
                                               @PathVariable() Long reqId) {
        log.info("Получен PATCH запрос к эндпоинту /users/{}/events/{}/requests/{}/reject",
                userId, eventId, reqId);
        return RequestMapper.toRequestDto(requestService.rejectRequest(userId, eventId, reqId));
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
