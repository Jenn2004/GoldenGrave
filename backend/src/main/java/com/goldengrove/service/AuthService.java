package com.goldengrove.service;

import com.goldengrove.dto.AuthResponse;
import com.goldengrove.dto.LoginRequest;
import com.goldengrove.dto.RegisterRequest;
import com.goldengrove.entity.Role;
import com.goldengrove.entity.User;
import com.goldengrove.exception.BadRequestException;
import com.goldengrove.repository.UserRepository;
import com.goldengrove.security.JwtUtil;
import com.goldengrove.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userRepository.save(user);
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtil.generateToken(principal);
        return buildResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtUtil.generateToken(principal);
        User user = userRepository.findByEmail(principal.getEmail()).orElseThrow();
        return buildResponse(user, token);
    }

    private AuthResponse buildResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
