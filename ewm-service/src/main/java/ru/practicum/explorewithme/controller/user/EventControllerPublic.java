package ru.practicum.explorewithme.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.event.dto.EventFullDto;
import ru.practicum.explorewithme.model.event.dto.EventShortDto;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.util.ValidationPageParam;
import ru.practicum.explorewithme.util.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventControllerPublic {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен GET запрос к эндпоинту /events/{}", id);
        eventService.makeEndpointHit(request);
        return EventMapper.toEventFullDto(eventService.getEventByIdPublic(id));
    }

    @GetMapping()
    public Collection<EventShortDto> getAllEventsByPublic(@RequestParam(required = false) String text,
                                                          @RequestParam(required = false) List<Long> categories,
                                                          @RequestParam(required = false) Boolean paid,
                                                          @RequestParam(required = false) String rangeStart,
                                                          @RequestParam(required = false) String rangeEnd,
                                                          @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                          @RequestParam(required = false) String sort,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(defaultValue = "10") Integer size,
                                                          HttpServletRequest request) {
        eventService.makeEndpointHit(request);
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /events?text={}categories{}paid={}rangeStart={}rangEnd{}" +
                "onlyAvailable={}sort={}from={}size={}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return eventService.getAllEventsByPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, pageRequest);
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
