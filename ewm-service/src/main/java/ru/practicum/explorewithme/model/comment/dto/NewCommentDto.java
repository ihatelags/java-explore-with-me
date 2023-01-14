package ru.practicum.explorewithme.model.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCommentDto {
    @NotBlank
    private String comment;
    @NotNull
    private Long event;
}
