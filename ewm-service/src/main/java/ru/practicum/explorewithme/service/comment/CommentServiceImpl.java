package ru.practicum.explorewithme.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.BadRequestException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.comment.Comment;
import ru.practicum.explorewithme.model.comment.dto.CommentDto;
import ru.practicum.explorewithme.model.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.model.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.util.mapper.CommentMapper;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Comment createComment(Long userId, @NotNull NewCommentDto newCommentDto) {
        Comment comment = CommentMapper.toComment(newCommentDto);
        comment.setCreated(LocalDateTime.now());

        Event event = eventRepository.findById(newCommentDto.getEvent())
                .orElseThrow(() -> new NotFoundException("Не найдено событие " + newCommentDto.getEvent()));
        comment.setEvent(event);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ид " + userId));
        comment.setCommentator(user);

        Comment commentDB = commentRepository.save(comment);
        log.info("Успешно создан коммент с ид " + commentDB.getId());
        return commentDB;
    }

    @Override
    public Comment updateCommentByAdmin(@NotNull UpdateCommentDto updateCommentDto) {
        Long commentId = updateCommentDto.getId();
        Comment comment = getCommentById(commentId);
        if (updateCommentDto.getComment() != null) {
            comment.setText(updateCommentDto.getComment());
        }
        Comment commentDB = commentRepository.save(comment);
        log.info("Успешно обновлен админом коммент с ид " + commentDB.getId());
        return commentDB;
    }

    @Override
    public Comment updateCommentByUser(Long userId, @NotNull UpdateCommentDto updateCommentDto) {
        Comment comment = getCommentById(updateCommentDto.getId());
        if (!comment.getCommentator().getId().equals(userId)) {
            throw new BadRequestException("Нельзя обновлять чужие комменты. Создатель коммента: "
                    + comment.getCommentator().getId());
        }
        if (updateCommentDto.getComment() != null) {
            comment.setText(updateCommentDto.getComment());
        }
        Comment commentDB = commentRepository.save(comment);
        log.info("Успешно обновлен пользователем коммент с ид " + commentDB.getId());
        return commentDB;
    }

    @Override
    public Comment getCommentById(Long commentId) {
        log.info("Запрос на получение коммента {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не найден коммент с ид " + commentId));
    }

    @Override
    public Collection<CommentDto> getCommentsByAdmin(List<Long> users, String rangeStart, String rangeEnd,
                                                     Integer from, Integer size) {
        final PageRequest pageRequest = findPageRequest(from, size);
        log.info("Запрос на получение комментов админом");
        LocalDateTime start = (rangeStart.isBlank()) ? LocalDateTime.now() :
                LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = (rangeEnd.isBlank()) ? LocalDateTime.now().plusYears(50) :
                LocalDateTime.parse(rangeEnd, FORMATTER);
        List<User> userEntities = userRepository.getUsersFromIds(users);

        Page<Comment> comments = commentRepository.getAllCommentsByAdmin(userEntities, start,
                end, pageRequest);

        return CommentMapper.toCommentDtoCollection(comments.getContent());
    }

    @Override
    public List<Comment> getAllCommentsByEvent(Long eventId, Integer from, Integer size) {
        final PageRequest pageRequest = findPageRequest(from, size);
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ид " + eventId));
        return commentRepository.getAllCommentsByEvent(eventId, pageRequest).stream()
                .collect(Collectors.toList());
    }

    @Override
    public Collection<CommentDto> getAllCommentsByUser(Long userId, String rangeStart, String rangeEnd,
                                                       Integer from, Integer size) {
        final PageRequest pageRequest = findPageRequest(from, size);
        log.info("Запрос на получение комментов пользователем");
        LocalDateTime start = (rangeStart.isBlank()) ? LocalDateTime.now() :
                LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = (rangeEnd.isBlank()) ? LocalDateTime.now().plusYears(50) :
                LocalDateTime.parse(rangeEnd, FORMATTER);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ид " + userId));

        Collection<Comment> comments = commentRepository.getAllCommentsByUser(userId, start,
                        end, pageRequest).stream().collect(Collectors.toList());

        return CommentMapper.toCommentDtoCollection(comments);
    }

    @Override
    public void deleteCommentByAdmin(Long commId) {
        getCommentById(commId);
        commentRepository.deleteById(commId);
        log.info("Коммент с ид " + commId + " успешно удален админом.");
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commId) {
        Comment comment = getCommentById(commId);
        if (!comment.getCommentator().getId().equals(userId)) {
            throw new BadRequestException("Нельзя удалять чужие комменты. Создатель коммента: "
                    + comment.getCommentator().getId());
        }
        commentRepository.deleteById(commId);
        log.info("Коммент с ид " + commId + " успешно удален пользователем.");
    }

    public PageRequest findPageRequest(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
