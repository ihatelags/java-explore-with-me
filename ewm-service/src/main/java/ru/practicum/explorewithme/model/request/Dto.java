package ru.practicum.explorewithme.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Dto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ParticipantRequestDto {
        private Long id;
        @NotNull
        private Long event;
        @NotNull
        private Long requester;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime created;
        @NotNull
        private RequestStatus status;
    }
}
