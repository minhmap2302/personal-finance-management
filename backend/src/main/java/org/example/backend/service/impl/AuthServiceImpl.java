package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.exception.ConflictException;
import org.example.backend.model.dto.request.LoginRequest;
import org.example.backend.model.dto.request.RegisterRequest;
import org.example.backend.model.dto.response.AuthResponse;
import org.example.backend.model.dto.response.UserResponse;
import org.example.backend.model.entity.RevokedToken;
import org.example.backend.model.entity.User;
import org.example.backend.repository.RevokedTokenRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.jwt.JwtService;
import org.example.backend.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Xử lý nghiệp vụ đăng ký, đăng nhập và đăng xuất (thu hồi token).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Xử lý đăng ký tài khoản với email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email '" + request.getEmail() + "' đã được sử dụng");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên người dùng '" + request.getUsername() + "' đã được sử dụng");
        }

        // Mã hóa mật khẩu bằng BCrypt trước khi lưu database
        User newUser = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .username(request.getUsername().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Đăng ký thành công tài khoản ID: {}", savedUser.getId());

        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getUsernameOrEmail().trim();
        log.info("Xử lý đăng nhập với: {}", identifier);

        // 1. Tìm kiếm User theo email hoặc username
        User user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        // 2. Xác thực mật khẩu thông qua AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        // 3. Tạo JWT Access Token
        String token = jwtService.generateToken(user);
        log.info("Người dùng '{}' đăng nhập thành công", user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Header Authorization không hợp lệ khi logout");
            return;
        }

        String token = authHeader.substring(7);

        // Nếu token chưa có trong danh sách blacklist thì lưu vào
        if (!revokedTokenRepository.existsByToken(token)) {
            LocalDateTime expiryDate;
            try {
                expiryDate = jwtService.extractExpirationAsLocalDateTime(token);
            } catch (Exception e) {
                // Nếu token đã hết hạn hoặc không giải mã được thì gán thời gian hiện tại
                expiryDate = LocalDateTime.now();
            }

            RevokedToken revokedToken = RevokedToken.builder()
                    .token(token)
                    .expiryDate(expiryDate)
                    .revokedAt(LocalDateTime.now())
                    .build();

            revokedTokenRepository.save(revokedToken);
            log.info("Đã thu hồi token thành công");
        }

        SecurityContextHolder.clearContext();
    }
}
