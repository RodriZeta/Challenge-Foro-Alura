package com.foro.web.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopicCreateDTO {
    @NotBlank private String title;
    @NotBlank private String message;
    @NotNull  private Long courseId;
}
