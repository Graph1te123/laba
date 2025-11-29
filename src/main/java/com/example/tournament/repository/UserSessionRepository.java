package com.example.tournament.repository;

import com.example.tournament.model.UserSession;
import com.example.tournament.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshToken(String refreshToken);
    Optional<UserSession> findByAccessToken(String accessToken);
    Optional<UserSession> findByRefreshTokenAndStatus(String refreshToken, SessionStatus status);
    List<UserSession> findByUserIdAndStatus(Long userId, SessionStatus status);
    void deleteByRefreshToken(String refreshToken);
}
