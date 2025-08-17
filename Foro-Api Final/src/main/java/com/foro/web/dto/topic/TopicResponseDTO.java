package com.foro.web.dto.topic;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopicResponseDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private String status;
    private String author;
    private String course;
}
