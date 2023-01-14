package ru.practicum.explorewithme.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.comment.dto.CommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;
import ru.practicum.explorewithme.util.ValidationPageParam;
import ru.practicum.explorewithme.util.mapper.CommentMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/events")
public class CommentControllerPublic {

    private final CommentService commentService;

    @Autowired
    public CommentControllerPublic(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{eventId}/comments/{commentId}")
    public CommentDto getCommentByEventById(@PathVariable Long eventId,
                                            @PathVariable Long commentId) {
        log.info("Получен GET запрос к эндпоинту /events/{}/comments/{}", eventId, commentId);
        return CommentMapper.toCommentDto(commentService.getCommentById(commentId));
    }

    @GetMapping("/{eventId}/comments")
    public Collection<CommentDto> getAllCommentsByEvent(@PathVariable Long eventId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /events/{}/comments?from={}size={}",
                eventId, from, size);
        return CommentMapper.toCommentDtoCollection(commentService.getAllCommentsByEvent(eventId, from, size));
    }

    private void validatePage(Integer from, Integer size) {
        ValidationPageParam validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}
