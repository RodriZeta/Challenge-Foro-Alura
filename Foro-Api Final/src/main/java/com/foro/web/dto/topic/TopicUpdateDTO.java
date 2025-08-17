package com.foro.web.dto.topic;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopicUpdateDTO {
    @NotBlank private String title;
    @NotBlank private String message;
    private String status; // OPEN/CLOSED/DELETED
}
