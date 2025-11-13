// src/main/java/com/example/tournament/controller/StandingController.java
package com.example.tournament.controller;

import com.example.tournament.model.Standing;
import com.example.tournament.service.StandingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/standings")
public class StandingController {

    private final StandingService standingService;

    public StandingController(StandingService standingService) {
        this.standingService = standingService;
    }

    @GetMapping
    public ResponseEntity<List<Standing>> getAllStandings() {
        return ResponseEntity.ok(standingService.getAllStandings());
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateStandings() {
        standingService.updateStandings();
        return ResponseEntity.ok(Map.of("message", "Турнирная таблица успешно пересчитана"));
    }

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<Standing>> getTopTeams(@PathVariable int limit) {
        return ResponseEntity.ok(standingService.getTopTeams(limit));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetStandings() {
        standingService.resetStandings();
        return ResponseEntity.ok(Map.of("message", "Статистика успешно сброшена"));
    }
}