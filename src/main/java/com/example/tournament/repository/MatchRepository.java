package com.example.tournament.repository;

import com.example.tournament.model.Match;
import com.example.tournament.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // Проверяет, есть ли матч с участием одной из этих команд в ту же дату
    boolean existsByHomeTeamOrAwayTeamAndMatchDate(Team homeTeam, Team awayTeam, LocalDateTime matchDate);
}

