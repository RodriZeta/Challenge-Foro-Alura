package com.foro.web.controller;

import com.foro.domain.course.Course;
import com.foro.domain.course.CourseRepository;
import com.foro.domain.topic.Topic;
import com.foro.domain.topic.TopicRepository;
import com.foro.domain.topic.TopicStatus;
import com.foro.domain.user.User;
import com.foro.domain.user.UserRepository;
import com.foro.web.dto.topic.TopicCreateDTO;
import com.foro.web.dto.topic.TopicResponseDTO;
import com.foro.web.dto.topic.TopicUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private TopicResponseDTO toDto(Topic t) {
        return TopicResponseDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .message(t.getMessage())
                .createdAt(t.getCreatedAt())
                .status(t.getStatus().name())
                .author(t.getAuthor().getName())
                .course(t.getCourse().getName())
                .build();
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> list() {
        List<TopicResponseDTO> list = topicRepository.findAll().stream()
                .map(this::toDto).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> getById(@PathVariable Long id) {
        Optional<Topic> opt = topicRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(opt.get()));
    }

    @PostMapping
    public ResponseEntity<TopicResponseDTO> create(@RequestBody @Valid TopicCreateDTO body, Authentication auth) {
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User author = userRepository.findByEmail(email).orElseThrow();
        Course course = courseRepository.findById(body.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));

        Topic t = Topic.builder()
                .title(body.getTitle())
                .message(body.getMessage())
                .author(author)
                .course(course)
                .status(TopicStatus.OPEN)
                .build();
        topicRepository.save(t);
        return ResponseEntity.created(URI.create("/api/topics/" + t.getId())).body(toDto(t));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> update(@PathVariable Long id, @RequestBody @Valid TopicUpdateDTO body) {
        Optional<Topic> opt = topicRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Topic t = opt.get();
        t.setTitle(body.getTitle());
        t.setMessage(body.getMessage());
        if (body.getStatus() != null) {
            try {
                t.setStatus(TopicStatus.valueOf(body.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) { /* status inválido: se ignora */ }
        }
        topicRepository.save(t);
        return ResponseEntity.ok(toDto(t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!topicRepository.existsById(id)) return ResponseEntity.notFound().build();
        topicRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
