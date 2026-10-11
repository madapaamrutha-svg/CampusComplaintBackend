package com.campuscomplaint.campuscomplaintbackend.service;

import com.campuscomplaint.campuscomplaintbackend.dto.LoginRequest;
import com.campuscomplaint.campuscomplaintbackend.dto.RegisterRequest;
import com.campuscomplaint.campuscomplaintbackend.entity.User;
import com.campuscomplaint.campuscomplaintbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public String register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String role = request.getRole().trim().toUpperCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        User.Role selectedRole;

        try {
            selectedRole = User.Role.valueOf(role);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Please select a valid account type.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole(selectedRole);
        user.setApproved(true);

        userRepository.save(user);

        return "Registration successful. You can now log in.";
    }

    public Map<String, String> login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password."));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password.");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        return Map.of(
                "message", "Login successful.",
                "id", String.valueOf(user.getId()),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "token", token
        );
    }
}