package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.dto.ViewStatsDto;
import ru.practicum.explorewithme.service.StatsService;
import ru.practicum.explorewithme.util.mapper.EndpointHitMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto createEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Received request to endpoint POST/hit");
        return EndpointHitMapper.toEndpointHitDto(statsService.createEndpointHit(endpointHitDto));
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getEndpointHits(@RequestParam() String start,
                                              @RequestParam() String end,
                                              @RequestParam List<String> uris,
                                              @RequestParam (defaultValue = "false") Boolean unique) {
        log.info("Received request to endpoint GET/stats?start={}end{}uris={}unique{}",
                                          start, end, uris.get(0), unique);
        return statsService.getEndpointHits(start, end, uris, unique);
    }
}
