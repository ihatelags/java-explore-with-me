package ru.practicum.explorewithme.service.event;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public interface EventService {

    Event createEvent(Long userId, NewEventDto newEventDto);

    Event updateEvent(Long userId, UpdateEventRequest updateEventRequest);

    Event editEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    Event cancelEvent(Long userId, Long eventId);

    Event publishEvent(Long eventId);

    Event rejectEvent(Long eventId);

    Collection<EventShortDto> getAllEventsByUser(Long userId, PageRequest pageRequest);

    Collection<EventFullDto> getAllEventsByAdmin(List<Long> users,
                                                 List<String> states,
                                                 List<Long> categories,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 PageRequest pageRequest);

    Collection<EventShortDto> getAllEventsByPublic(String text, List<Long> categories, Boolean paid,
                                                         String rangeStart, String rangeEnd,
                                                         Boolean onlyAvailable, String sort,
                                                         PageRequest pageRequest);

    Event getEventById(Long eventId);

    Event getEventByIdPublic(Long id);

    void makeEndpointHit(HttpServletRequest request);
}
