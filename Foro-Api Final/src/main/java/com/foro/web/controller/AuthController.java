package com.foro.web.controller;

import com.foro.domain.user.Profile;
import com.foro.domain.user.ProfileRepository;
import com.foro.domain.user.User;
import com.foro.domain.user.UserRepository;
import com.foro.security.JwtService;
import com.foro.web.dto.auth.AuthResponse;
import com.foro.web.dto.auth.LoginRequest;
import com.foro.web.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(409).build();
        }
        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        Profile userRole = profileRepository.findByName("USER")
                .orElseGet(() -> profileRepository.save(Profile.builder().name("USER").build()));
        u.getProfiles().add(userRole);
        userRepository.save(u);

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(u.getEmail())
                        .password(u.getPassword())
                        .authorities("ROLE_USER")
                        .build()
        );
        return ResponseEntity.created(URI.create("/api/users/" + u.getId()))
                .body(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.User) auth.getPrincipal());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
