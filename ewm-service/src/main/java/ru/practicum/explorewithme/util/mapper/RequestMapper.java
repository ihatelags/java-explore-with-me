package ru.practicum.explorewithme.util.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.model.request.Request;
import ru.practicum.explorewithme.model.request.dto.RequestDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public static List<RequestDto> toRequestDtoCollection(Collection<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public static RequestDto toRequestDto(Request request) {
       return RequestDto.builder()
           .id(request.getId())
           .event(request.getEvent().getId())
           .requester(request.getRequester().getId())
           .created(request.getCreated())
           .status(request.getStatus())
           .build();
    }

}
