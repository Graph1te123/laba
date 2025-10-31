package com.example.tournament.controller;

import com.example.tournament.dto.MatchRequest;
import com.example.tournament.model.Match;
import com.example.tournament.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // ✅ Создание матча
    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody MatchRequest request) {
        try {
            Match match = matchService.createMatch(request);
            return ResponseEntity.ok(match);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    // ✅ Получить все матчи
    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    // ✅ Получить матч по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matchService.getMatchById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Удалить матч
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        try {
            matchService.deleteMatch(id);
            return ResponseEntity.ok(Map.of("message", "Match deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Получить все матчи конкретной команды
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Match>> getMatchesByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(matchService.getMatchesByTeam(teamId));
    }

    // ✅ Получить все предстоящие матчи
    @GetMapping("/upcoming")
    public ResponseEntity<List<Match>> getUpcomingMatches() {
        return ResponseEntity.ok(matchService.getUpcomingMatches());
    }
}
