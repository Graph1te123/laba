package com.example.tournament.service;

import com.example.tournament.model.User;
import com.example.tournament.model.UserSession;
import com.example.tournament.model.SessionStatus;
import com.example.tournament.repository.UserSessionRepository;
import com.example.tournament.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Transactional
    public Map<String, Object> createTokenPair(User user) {
        String email = user.getEmail();

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        UserSession session = new UserSession(user, refreshToken, accessToken, expiresAt);
        userSessionRepository.save(session);

        Map<String, Object> tokenPair = new HashMap<>();
        tokenPair.put("accessToken", accessToken);
        tokenPair.put("refreshToken", refreshToken);
        tokenPair.put("expiresIn", 900);
        tokenPair.put("tokenType", "Bearer");

        return tokenPair;
    }

    @Transactional
    public Map<String, Object> refreshTokenPair(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }

        UserSession session = userSessionRepository.findByRefreshTokenAndStatus(
                refreshToken, SessionStatus.ACTIVE
        ).orElseThrow(() -> new IllegalArgumentException("Refresh token not found or expired"));

        if (session.isExpired()) {
            session.setStatus(SessionStatus.EXPIRED);
            userSessionRepository.save(session);
            throw new IllegalArgumentException("Session expired");
        }

        User user = session.getUser();
        String email = user.getEmail();

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setRefreshedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        userSessionRepository.save(session);

        Map<String, Object> tokenPair = new HashMap<>();
        tokenPair.put("accessToken", newAccessToken);
        tokenPair.put("refreshToken", newRefreshToken);
        tokenPair.put("expiresIn", 900);
        tokenPair.put("tokenType", "Bearer");

        return tokenPair;
    }

    @Transactional
    public void revokeSession(String accessToken) {
        UserSession session = userSessionRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        session.setStatus(SessionStatus.REVOKED);
        session.setRevokedAt(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    public boolean isAccessTokenValid(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return false;
        }

        String tokenType = jwtTokenProvider.getTokenType(accessToken);
        if (!"ACCESS".equals(tokenType)) {
            return false;
        }

        UserSession session = userSessionRepository.findByAccessToken(accessToken)
                .orElse(null);

        return session != null && session.isActive();
    }
}
