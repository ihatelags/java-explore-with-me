package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {

    EndpointHit createEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getEndpointHits(String start, String end, List<String> uris, Boolean unique);
}
