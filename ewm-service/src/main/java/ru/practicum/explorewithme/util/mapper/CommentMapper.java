package ru.practicum.explorewithme.util.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.model.comment.Comment;
import ru.practicum.explorewithme.model.comment.dto.CommentDto;
import ru.practicum.explorewithme.model.comment.dto.NewCommentDto;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public static Collection<CommentDto> toCommentDtoCollection(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(comment.getEvent().getId())
                .user(comment.getUser().getId())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getComment())
                .build();
    }
}
