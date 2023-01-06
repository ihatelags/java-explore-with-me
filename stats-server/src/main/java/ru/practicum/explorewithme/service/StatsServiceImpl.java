package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.dto.ViewStatsDto;
import ru.practicum.explorewithme.repository.StatsRepository;
import ru.practicum.explorewithme.util.mapper.EndpointHitMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    @Override
    public EndpointHit createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("EndpointHit id " + endpointHit.getId() + " has successfully created.");
        return endpointHit;
    }

    @Override
    public List<ViewStatsDto> getEndpointHits(String startDate, String endDate, List<String> uris, Boolean unique) {
        if (!uris.isEmpty()) {
            String[] eventsUris = uris.get(0).split(" ");
            uris = List.of(eventsUris);
        }

        LocalDateTime start = LocalDateTime.parse(URLDecoder.decode(startDate, StandardCharsets.UTF_8), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(URLDecoder.decode(endDate, StandardCharsets.UTF_8), FORMATTER);
        List<ViewStatsDto> result;
        if (!uris.isEmpty()) {
            if (unique) {
                result = statsRepository.getWithUrisUnique(start, end, uris);
            } else {
                result = statsRepository.getWithUris(start, end, uris);
            }
        } else {
            if (unique) {
                result = statsRepository.getWithoutUrisUnique(start, end);
            } else {
                result = statsRepository.getWithoutUris(start, end);
            }
        }
        return result;
    }

}
