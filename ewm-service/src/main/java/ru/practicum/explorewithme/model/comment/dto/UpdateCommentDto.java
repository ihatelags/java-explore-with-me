package ru.practicum.explorewithme.model.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCommentDto {
    private Long id;
    @NotBlank
    private String comment;
}
