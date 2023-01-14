package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.comment.dto.CommentDto;
import ru.practicum.explorewithme.model.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;
import ru.practicum.explorewithme.util.ValidationPageParam;
import ru.practicum.explorewithme.util.mapper.CommentMapper;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/comments")
public class CommentControllerAdmin {

    private final CommentService commentService;

    @GetMapping()
    public Collection<CommentDto> getCommentsByAdmin(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(defaultValue = "") String rangeStart,
                                                     @RequestParam(defaultValue = "") String rangeEnd,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @NotNull @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /admin/comments?users={}" +
                "rangeStart{}rangeEnd{}from={}size={}", users, rangeStart, rangeEnd, from, size);
        return commentService.getCommentsByAdmin(users, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("Получен GET запрос к эндпоинту /admin/comments/{}", commentId);
        return CommentMapper.toCommentDto(commentService.getCommentById(commentId));
    }

    @PutMapping("/{commentId}")
    public CommentDto updateCommentByAdmin(@PathVariable Long commentId,
                                           @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Получен PUT запрос к эндпоинту /admin/comments/{}", commentId);
        return CommentMapper.toCommentDto(commentService.updateCommentByAdmin(updateCommentDto));
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Получен DELETE запрос к эндпоинту /admin/comments/{}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    private void validatePage(Integer from, Integer size) {
        ValidationPageParam validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}
