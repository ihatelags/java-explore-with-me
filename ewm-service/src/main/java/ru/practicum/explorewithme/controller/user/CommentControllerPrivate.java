package ru.practicum.explorewithme.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.comment.dto.CommentDto;
import ru.practicum.explorewithme.model.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.model.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;
import ru.practicum.explorewithme.util.ValidationPageParam;
import ru.practicum.explorewithme.util.mapper.CommentMapper;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable Long userId,
                                    @RequestBody NewCommentDto newCommentDto) {
        log.info("Получен POST запрос к эндпоинту users/{}/comments", userId);
        return CommentMapper.toCommentDto(commentService.createComment(userId, newCommentDto));
    }

    @PatchMapping
    public CommentDto updateCommentByUser(@PathVariable Long userId,
                                          @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Получен PATCH запрос к эндпоинту users/{}/comments", userId);
        return CommentMapper.toCommentDto(commentService.updateCommentByUser(userId, updateCommentDto));
    }

    @GetMapping
    public Collection<CommentDto> getAllCommentsByUser(@PathVariable Long userId,
                                                       @RequestParam(defaultValue = "") String rangeStart,
                                                       @RequestParam(defaultValue = "") String rangeEnd,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту users/{}/comments?rangeStart={}rangeEnd={}from={}size={}",
                userId, rangeStart, rangeEnd, from, size);
        return commentService.getAllCommentsByUser(userId, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Long userId,
                                     @PathVariable Long commentId) {
        log.info("Получен GET запрос к эндпоинту users/{}/comments/{}", userId, commentId);
        return CommentMapper.toCommentDto(commentService.getCommentById(commentId));
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByUser(@PathVariable Long userId,
                                    @PathVariable Long commentId) {
        log.info("Получен DELETE запрос к эндпоинту users/{}/comments/{}", userId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
    }

    private void validatePage(Integer from, Integer size) {
        ValidationPageParam validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}
