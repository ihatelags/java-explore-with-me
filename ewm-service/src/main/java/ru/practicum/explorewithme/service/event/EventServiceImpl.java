package ru.practicum.explorewithme.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.BadRequestException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.category.Category;
import ru.practicum.explorewithme.model.endpointhit.EndpointHitEwmDto;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventStatus;
import ru.practicum.explorewithme.model.event.dto.*;
import ru.practicum.explorewithme.model.request.RequestStatus;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.util.client.EventClient;
import ru.practicum.explorewithme.util.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    public static final String NAME_OF_APP = "ewm-main-service";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final RequestRepository requestRepository;
    private final EventClient eventClient;

    @Override
    public Event createEvent(Long userId, @NotNull NewEventDto newEventDto) {
        Event event = EventMapper.toEvent(newEventDto);
        if (event.getAnnotation() == null || event.getAnnotation().isBlank()) {
            throw new BadRequestException("Аннотация не должна быть пустой.");
        }
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        event.setCategory(category);

        User user = getUserById(userId);
        event.setInitiator(user);

        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());

        event.setState(EventStatus.PENDING);

        Event eventDB = eventRepository.save(event);
        log.info("Событие с ид " + eventDB.getId() + " успешно создано.");
        return eventDB;
    }

    private Event updateParamEventByUser(Event event, @NotNull UpdateEventRequest updateEventRequest) {
        Category category = getCategoryById(updateEventRequest.getCategory());
        event.setCategory(category);
        Optional.ofNullable(updateEventRequest.getAnnotation())
                .ifPresent(event::setAnnotation);

        Optional.ofNullable(updateEventRequest.getDescription())
                .ifPresent(event::setDescription);

        Optional.ofNullable(updateEventRequest.getEventDate())
                .ifPresent(event::setEventDate);

        Optional.ofNullable(updateEventRequest.getPaid())
                .ifPresent(event::setPaid);

        Optional.ofNullable(updateEventRequest.getParticipantLimit())
                .ifPresent(event::setParticipantLimit);

        Optional.ofNullable(updateEventRequest.getTitle())
                .ifPresent(event::setTitle);
        return event;
    }

    @Override
    public Event updateEvent(Long userId, @NotNull UpdateEventRequest updateEventRequest) {
        Event event = getEventById(updateEventRequest.getEventId());
        Event eventDB = eventRepository.save(updateParamEventByUser(event, updateEventRequest));
        log.info("Событие с ид " + eventDB.getId() + " успешно обновлено.");
        return eventDB;
    }

    private Event updateParamEventByAdmin(Event event, @NotNull AdminUpdateEventRequest adminUpdateEventRequest) {
        if (adminUpdateEventRequest.getCategory() != null) {
            Category category = categoryRepository.findById(adminUpdateEventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        Optional.ofNullable(adminUpdateEventRequest.getAnnotation())
                .ifPresent(event::setAnnotation);

        Optional.ofNullable(adminUpdateEventRequest.getDescription())
                .ifPresent(event::setDescription);

        Optional.ofNullable(adminUpdateEventRequest.getEventDate())
                .ifPresent(event::setEventDate);

        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLat(adminUpdateEventRequest.getLocation().getLat());
            event.setLon(adminUpdateEventRequest.getLocation().getLon());
        }

        Optional.ofNullable(adminUpdateEventRequest.getPaid())
                .ifPresent(event::setPaid);

        Optional.ofNullable(adminUpdateEventRequest.getParticipantLimit())
                .ifPresent(event::setParticipantLimit);

        Optional.ofNullable(adminUpdateEventRequest.getRequestModeration())
                .ifPresent(event::setRequestModeration);

        Optional.ofNullable(adminUpdateEventRequest.getTitle())
                .ifPresent(event::setTitle);
        return event;
    }

    @Override
    public Event editEventByAdmin(Long eventId, @NotNull AdminUpdateEventRequest adminUpdateEventRequest) {
        Event event = getEventById(eventId);
        Event eventDB = eventRepository.save(updateParamEventByAdmin(event, adminUpdateEventRequest));
        log.info("Событие с ид " + eventDB.getId() + " успешно обновлено.");
        return eventDB;
    }

    @Override
    public Event cancelEvent(Long userId, Long eventId) {
        Event event = getEventById(eventId);
        event.setState(EventStatus.CANCELED);
        Event eventDB = eventRepository.save(event);
        log.info("Событие с ид " + eventDB.getId() + " успешно отменено.");
        return eventDB;
    }

    @Override
    public Event publishEvent(Long eventId) {
        Event event = getEventById(eventId);
        event.setState(EventStatus.PUBLISHED);
        Event eventDB = eventRepository.save(event);
        log.info("Событие с ид " + eventDB.getId() + " успешно опубликовано.");
        return eventDB;
    }

    @Override
    public Event rejectEvent(Long eventId) {
        Event event = getEventById(eventId);
        event.setState(EventStatus.CANCELED);
        Event eventDB = eventRepository.save(event);
        log.info("Событие с ид " + eventDB.getId() + " успешно отклонено.");
        return eventDB;
    }

    @Override
    public Collection<EventShortDto> getAllEventsByUser(Long userId, PageRequest pageRequest) {
        log.info("Запрос на получение событий пользователя");
        User user = getUserById(userId);
        Collection<Event> eventCollection = eventRepository.getAllEventsByUser(user.getId(), pageRequest).stream()
                .collect(Collectors.toList());

        return EventMapper.toEventShortDtoCollection(eventCollection);
    }

    @Override
    public List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<String> states,
                                                        List<Long> categories,
                                                        String rangeStart, String rangeEnd,
                                                        PageRequest pageRequest) {
        log.info("Запрос на получение событий админом");
        LocalDateTime start = (rangeStart.isBlank()) ? LocalDateTime.now() :
                LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = (rangeEnd.isBlank()) ? LocalDateTime.now().plusYears(50) :
                LocalDateTime.parse(rangeEnd, FORMATTER);

        List<EventStatus> statesEnum;
        if (states != null) {
            statesEnum = states.stream()
                    .map(EventStatus::valueOf)
                    .collect(Collectors.toList());
        } else {
            statesEnum = new ArrayList<>(Arrays.asList(EventStatus.values()));
        }
        List<Category> categoryEntities = categoryRepository.getCategoriesFromIds(categories);
        List<User> userEntities = userRepository.getUsersFromIds(users);

        return eventRepository.getAllEventsByAdmin(userEntities, statesEnum, categoryEntities, start, end,
                        pageRequest)
                .stream()
                .map(EventMapper::toEventFullDto)
                .map(this::setConfirmedRequestsAndViewsEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllEventsByPublic(String text, List<Long> categories, Boolean paid,
                                                          String rangeStart, String rangeEnd,
                                                          Boolean onlyAvailable, String sort,
                                                          PageRequest pageRequest) {
        log.info("Запрос на получение событий для всех");
        LocalDateTime start = (rangeStart == null) ? LocalDateTime.now() :
                LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = (rangeEnd == null) ? LocalDateTime.now().plusYears(50) :
                LocalDateTime.parse(rangeEnd, FORMATTER);

        List<Category> categoryEntities = categoryRepository.getCategoriesFromIds(categories);
        Page<Event> events;
         if (text.isBlank()) {
            if (onlyAvailable.equals(true)) {
                events = eventRepository.getAllEventsPublicByEventDateAvailableAll(text, categoryEntities, paid,
                        start, end, pageRequest);
            } else {
                events = eventRepository.getAllEventsPublicByEventDateAll(text, categoryEntities, paid,
                        start, end, pageRequest);
            }

        } else {
            if (onlyAvailable.equals(true)) {
                events = eventRepository.getAllEventsPublicByEventDateAvailableText(text, categoryEntities, paid,
                        start, end, pageRequest);
            } else {
                events = eventRepository.getAllEventsPublicByEventDateText(text, categoryEntities, paid,
                        start, end, pageRequest);
            }
        }

        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDto)
                .map(this::setConfirmedRequestsAndViewsEventShortDto)
                .collect(Collectors.toList());

        if (sort != null && sort.equals("VIEWS")) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return eventShortDtos;
    }

    @Override
    public Event getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ид " + eventId));
        log.info("Запрос на получение события {}", eventId);
        return event;
    }

    @Override
    public Event getEventByIdPublic(Long eventId) {
        Event event = getEventById(eventId);
        event.setViews(getViews(eventId));
        event.setConfirmedRequests(getConfirmedRequests(event.getId()));
        return event;
    }

    private Long getConfirmedRequests(Long eventId) {
        RequestStatus status = RequestStatus.CONFIRMED;
        int count = requestRepository.countByEventIdAndStatus(eventId, status);
        return (long) count;
    }

    public void makeEndpointHit(HttpServletRequest request) {
        EndpointHitEwmDto endpointHitEwmDto = new EndpointHitEwmDto();
        endpointHitEwmDto.setApp(NAME_OF_APP);
        endpointHitEwmDto.setUri(request.getRequestURI());
        endpointHitEwmDto.setIp(request.getRemoteAddr());
        endpointHitEwmDto.setTimestamp(LocalDateTime.now().format(FORMATTER));
        log.info("Made Endpoint Hit");
        eventClient.createEndpointHit(endpointHitEwmDto);
    }

    public Long getViews(long eventId) {
        String start = LocalDateTime.now().minusYears(50).format(FORMATTER);
        String end = LocalDateTime.now().plusYears(50).format(FORMATTER);
        ResponseEntity<Object> responseEntity = eventClient.getEndpointHits(
                start,
                end,
                "/events/" + eventId,
                false);
        log.info("responseEntity {}", responseEntity.getBody());
        if (responseEntity.getBody().equals("")) {
            return (Long) ((LinkedHashMap) responseEntity.getBody()).get("hits");
        }
        return 0L;

    }

    private EventShortDto setConfirmedRequestsAndViewsEventShortDto(EventShortDto eventShortDto) {
        eventShortDto.setConfirmedRequests(getConfirmedRequests(eventShortDto.getId()));
        eventShortDto.setViews(getViews(eventShortDto.getId()));
        return eventShortDto;
    }

    public EventFullDto setConfirmedRequestsAndViewsEventFullDto(EventFullDto eventFullDto) {
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(getViews(eventFullDto.getId()));
        return eventFullDto;
    }

    private Category getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Не найдена категория " + catId));
        log.info("Запрос на получение категории {}", category.getName());
        return category;
    }

    private User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь " + userId));
        log.info("Запрос на получение пользователя {}", user.getName());
        return user;
    }

}
