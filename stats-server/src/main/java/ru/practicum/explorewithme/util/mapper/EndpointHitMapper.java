package ru.practicum.explorewithme.util.mapper;

import lombok.Data;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class EndpointHitMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp().format(FORMATTER))
                .build();
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        LocalDateTime timestamp = LocalDateTime.parse(endpointHitDto.getTimestamp(), FORMATTER);
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(timestamp)
                .build();
    }
}
