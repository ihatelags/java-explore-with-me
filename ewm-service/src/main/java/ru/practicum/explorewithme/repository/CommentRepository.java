package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.comment.Comment;
import ru.practicum.explorewithme.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c " +
            "from Comment c " +
            "where c.user in ?1 " +
            "and c.created between ?2 and ?3 " +
            "group by c.id, c.text, c.user.id, c.created, c.event.id " +
            "order by c.created desc")
    List<Comment> getAllCommentsByAdmin(List<User> userIds, LocalDateTime rangeStartFormatted,
                                        LocalDateTime rangeEndFormatted, Pageable pageable);

    @Query("select c " +
            "from Comment c " +
            "where c.user.id = ?1 " +
            "and c.created between ?2 and ?3 " +
            "group by c.id, c.text, c.user.id, c.created, c.event.id " +
            "order by c.created desc")
    List<Comment> getAllCommentsByUser(Long userId, LocalDateTime rangeStartFormatted,
                                       LocalDateTime rangeEndFormatted, Pageable pageable);

    @Query("select c " +
            "from Comment c " +
            "where c.event.id = ?1 " +
            "group by c.id, c.text, c.user.id, c.created, c.event.id " +
            "order by c.created desc")
    List<Comment> getAllCommentsByEvent(Long eventId, Pageable pageable);
}
