package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.explorewithme.model.dto.ViewStatsDto(hit.app, hit.uri, count(distinct hit.ip))"
            + " from EndpointHit hit"
            + " where hit.timestamp between :start and :end"
            + " and hit.uri in :uris"
            + " group by hit.app, hit.uri")
    List<ViewStatsDto> getWithUrisUnique(LocalDateTime start, LocalDateTime end,
                                         List<String> uris);

    @Query("select new ru.practicum.explorewithme.model.dto.ViewStatsDto(hit.app, hit.uri, count(hit.ip))"
            + " from EndpointHit hit"
            + " where hit.timestamp between :start and :end"
            + " and hit.uri in :uris"
            + " group by hit.app, hit.uri")
    List<ViewStatsDto> getWithUris(LocalDateTime start, LocalDateTime end,
                                   List<String> uris);

    @Query("select new ru.practicum.explorewithme.model.dto.ViewStatsDto(hit.app, hit.uri, count(distinct hit.ip))"
            + " from EndpointHit hit"
            + " where hit.timestamp between ?1 and ?2"
            + " group by hit.app, hit.uri")
    List<ViewStatsDto> getWithoutUrisUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.explorewithme.model.dto.ViewStatsDto(hit.app, hit.uri, count(hit.ip))"
            + " from EndpointHit hit"
            + " where hit.timestamp between ?1 and ?2"
            + " group by hit.app, hit.uri")
    List<ViewStatsDto> getWithoutUris(LocalDateTime start, LocalDateTime end);

}
