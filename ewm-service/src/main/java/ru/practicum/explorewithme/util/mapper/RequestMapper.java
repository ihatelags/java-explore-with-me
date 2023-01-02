package ru.practicum.explorewithme.util.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.model.request.Dto;
import ru.practicum.explorewithme.model.request.Request;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public static List<Dto.ParticipantRequestDto> toParticipantRequestDtoCollection(Collection<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toParticipantRequestDto)
                .collect(Collectors.toList());
    }

    public static Dto.ParticipantRequestDto toParticipantRequestDto(Request request) {
       return Dto.ParticipantRequestDto.builder()
           .id(request.getId())
           .event(request.getEvent().getId())
           .requester(request.getRequester().getId())
           .created(request.getCreated())
           .status(request.getStatus())
           .build();
    }

}
