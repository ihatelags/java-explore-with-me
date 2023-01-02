package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.category.Category;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventStatus;
import ru.practicum.explorewithme.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCategoryId(long catId);

    @Query("select e " +
            "from Event e " +
            "where e.initiator.id = ?1 " +
            "group by e.id " +
            "order by e.id desc")
    Page<Event> getAllEventsByUser(Long userId, Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "where e.initiator in ?1 " +
            "and e.state in ?2 " +
            "and e.category in ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "group by e.id " +
            "order by e.eventDate desc")
    Page<Event> getAllEventsByAdmin(List<User> users, List<EventStatus> states, List<Category> categoryEntities,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    PageRequest pageRequest);

    @Query("select e " +
            "from Event e " +
            "where upper(e.annotation) like upper(concat('%',?1,'%')) " +
            "or upper(e.description) like upper(concat('%',?1,'%')) " +
            "and e.category in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "and (e.participantLimit - e.confirmedRequests) > 0 " +
            "and e.state = ?6 " +
            "group by e.id " +
            "order by e.eventDate desc")
    Page<Event> getAllEventsPublicByEventDateAvailableText(String text, List<Category> categoryEntities, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           PageRequest pageRequest);

    @Query("select e " +
            "from Event e " +
            "where e.category in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "and (e.participantLimit - e.confirmedRequests) > 0 " +
            "and e.state = ?6 " +
            "group by e.id " +
            "order by e.eventDate desc")
    Page<Event> getAllEventsPublicByEventDateAvailableAll(String text, List<Category> categoryEntities, Boolean paid,
                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                          PageRequest pageRequest);


    @Query("select e " +
            "from Event e " +
            "where upper(e.annotation) like upper(concat('%',?1,'%')) " +
            "or upper(e.description) like upper(concat('%',?1,'%')) " +
            "and e.category in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "group by e.id " +
            "order by e.eventDate desc")
    Page<Event> getAllEventsPublicByEventDateText(String text, List<Category> categoryEntities, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  PageRequest pageRequest);


    @Query("select e " +
            "from Event e " +
            "where e.category in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "group by e.id " +
            "order by e.eventDate desc")
    Page<Event> getAllEventsPublicByEventDateAll(String text, List<Category> categoryEntities, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 PageRequest pageRequest);

}
