package org.example.backend.security.principal;

import lombok.RequiredArgsConstructor;
import org.example.backend.repository.RevokedTokenRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * Lấy id người dùng từ principal mà JwtAuthenticationFilter đã đặt vào SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final RevokedTokenRepository revokedTokenRepository;

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            // Chưa đăng nhập, hoặc anonymous
            throw new AuthenticationCredentialsNotFoundException("Chưa đăng nhập hoặc token không hợp lệ");
        }
        return principal.getId();
    }
}