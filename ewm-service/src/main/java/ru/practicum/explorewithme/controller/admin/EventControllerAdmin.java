package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewithme.model.event.dto.EventFullDto;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.util.ValidationPageParam;
import ru.practicum.explorewithme.util.mapper.EventMapper;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping()
    public Collection<EventFullDto> getAllEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) List<String> states,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(defaultValue = "") String rangeStart,
                                                        @RequestParam(defaultValue = "") String rangeEnd,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /admin/events?users={}states{}categories{}" +
                "rangeStart{}rangeEnd{}from={}size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return eventService.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        log.info("Получен PATCH запрос к эндпоинту /admin/events/{}/publish", eventId);
        return EventMapper.toEventFullDto(eventService.publishEvent(eventId));
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        log.info("Получен PATCH запрос к эндпоинту /admin/events/{}/reject", eventId);
        return EventMapper.toEventFullDto(eventService.rejectEvent(eventId));
    }

    @PutMapping("/{eventId}")
    public EventFullDto editEventByAdmin(@PathVariable Long eventId,
                                    @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("Получен PUT запрос к эндпоинту /admin/events/{}", eventId);
        return EventMapper.toEventFullDto(eventService.editEventByAdmin(eventId, adminUpdateEventRequest));
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
