package com.example.tournament.repository;

import com.example.tournament.model.Match;
import com.example.tournament.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {


    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Match m WHERE (m.homeTeam = :team OR m.awayTeam = :team) " +
            "AND m.matchDate = :matchDate")
    boolean existsTeamConflict(@Param("team") Team team, @Param("matchDate") LocalDateTime matchDate);


    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId")
    List<Match> findByTeamId(@Param("teamId") Long teamId);


    @Query("SELECT m FROM Match m WHERE m.matchDate > CURRENT_TIMESTAMP ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatches();
}

