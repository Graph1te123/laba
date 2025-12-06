package com.example.tournament.controller;

import com.example.tournament.dto.MatchRequest;
import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.model.Match;
import com.example.tournament.repository.MatchRepository;
import com.example.tournament.repository.TeamRepository;
import com.example.tournament.repository.VenueRepository;
import com.example.tournament.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private VenueRepository venueRepository;

    // --- CREATE (только ADMIN) ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Match> createMatch(@RequestBody MatchRequest request) {
        Match match = matchService.createMatch(request);
        return ResponseEntity.ok(match);
    }

    // --- READ (все авторизованные пользователи) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return ResponseEntity.ok(match);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Match> getUpcomingMatches() {
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findAll()
                .stream()
                .filter(match -> match.getMatchDate().isAfter(now))
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }

    @GetMapping("/upcoming/date/{date}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Match>> getUpcomingMatchesByDate(@PathVariable String date) {
        LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");

        List<Match> matchesOnDate = matchRepository.findAll()
                .stream()
                .filter(match -> match.getMatchDate().isAfter(startOfDay) &&
                        match.getMatchDate().isBefore(endOfDay))
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchesOnDate);
    }

    @GetMapping("/upcoming/team/{teamId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Match>> getUpcomingMatchesByTeam(@PathVariable Long teamId) {
        LocalDateTime now = LocalDateTime.now();
        List<Match> teamMatches = matchRepository.findAll()
                .stream()
                .filter(match -> match.getMatchDate().isAfter(now))
                .filter(match -> match.getHomeTeam().getId().equals(teamId) ||
                        match.getAwayTeam().getId().equals(teamId))
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());

        return ResponseEntity.ok(teamMatches);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Match>> getCompletedMatches() {
        LocalDateTime now = LocalDateTime.now();
        List<Match> completedMatches = matchRepository.findAll()
                .stream()
                .filter(match -> match.getMatchDate().isBefore(now))
                .sorted(Comparator.comparing(Match::getMatchDate).reversed())
                .collect(Collectors.toList());

        return ResponseEntity.ok(completedMatches);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Match>> getMatchesByTeam(@PathVariable Long teamId) {
        List<Match> teamMatches = matchRepository.findAll()
                .stream()
                .filter(match -> match.getHomeTeam().getId().equals(teamId) ||
                        match.getAwayTeam().getId().equals(teamId))
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());

        return ResponseEntity.ok(teamMatches);
    }

    @GetMapping("/stats/summary")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MatchSummary> getMatchSummary() {
        LocalDateTime now = LocalDateTime.now();
        List<Match> allMatches = matchRepository.findAll();

        long upcoming = allMatches.stream()
                .filter(m -> m.getMatchDate().isAfter(now))
                .count();
        long completed = allMatches.stream()
                .filter(m -> m.getMatchDate().isBefore(now))
                .count();

        MatchSummary summary = new MatchSummary(
                allMatches.size(),
                upcoming,
                completed
        );

        return ResponseEntity.ok(summary);
    }

    // --- UPDATE (только ADMIN) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Match> updateMatch(@PathVariable Long id, @RequestBody MatchRequest request) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));

        match.setHomeTeam(teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Home team not found with id: " + request.getHomeTeamId())));

        match.setAwayTeam(teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Away team not found with id: " + request.getAwayTeamId())));

        match.setVenue(venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + request.getVenueId())));

        match.setMatchDate(request.getMatchDate());
        match.setScoreHome(request.getScoreHome());
        match.setScoreAway(request.getScoreAway());

        Match updated = matchRepository.save(match);
        return ResponseEntity.ok(updated);
    }

    // --- DELETE (только ADMIN) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        matchRepository.delete(match);
        return ResponseEntity.noContent().build();
    }

    /**
     * Модель для статистики матчей
     */
    public static class MatchSummary {
        public int total;
        public long upcoming;
        public long completed;

        public MatchSummary(int total, long upcoming, long completed) {
            this.total = total;
            this.upcoming = upcoming;
            this.completed = completed;
        }
    }
}