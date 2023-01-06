package ru.practicum.explorewithme.util.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.dto.*;
import ru.practicum.explorewithme.model.event.location.Location;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<EventShortDto> toEventShortDtoCollection(Collection<Event> eventCollection) {
        return eventCollection.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(FORMATTER))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .build();
    }

    public static List<EventFullDto> toEventFullDtoCollection(Collection<Event> eventCollection) {
        return eventCollection.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public static EventFullDto toEventFullDto(Event event) {
        Location location = new Location();
        location.setLat(event.getLat());
        location.setLon(event.getLon());

        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .location(location)
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .participantLimit(event.getParticipantLimit())
                .paid(event.getPaid())
                .requestModeration(event.getRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .views(event.getViews())
                .initiator(event.getInitiator())
                .state(event.getState().toString())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

    public static Event toEvent(UpdateEventRequest updateEventRequest) {
        return Event.builder()
                .id(updateEventRequest.getEventId())
                .title(updateEventRequest.getTitle())
                .annotation(updateEventRequest.getAnnotation())
                .description(updateEventRequest.getDescription())
                .eventDate(updateEventRequest.getEventDate())
                .participantLimit(updateEventRequest.getParticipantLimit())
                .paid(updateEventRequest.getPaid())
                .build();
    }

    public static Event toEvent(AdminUpdateEventRequest adminUpdateEventRequest) {
        return Event.builder()
                .annotation(adminUpdateEventRequest.getAnnotation())
                .description(adminUpdateEventRequest.getDescription())
                .eventDate(adminUpdateEventRequest.getEventDate())
                .lat(adminUpdateEventRequest.getLocation().getLat())
                .lon(adminUpdateEventRequest.getLocation().getLon())
                .participantLimit(adminUpdateEventRequest.getParticipantLimit())
                .paid(adminUpdateEventRequest.getPaid())
                .requestModeration(adminUpdateEventRequest.getRequestModeration())
                .title(adminUpdateEventRequest.getTitle())
                .build();
    }
}
