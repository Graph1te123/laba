package com.example.tournament.controller;

import com.example.tournament.model.Standing;
import com.example.tournament.service.StandingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
public class StandingController {

    private final StandingService standingService;

    public StandingController(StandingService standingService) {
        this.standingService = standingService;
    }

    // 🔄 Обновление таблицы (можно вызвать из Postman или браузера)
    @GetMapping("/update")
    public ResponseEntity<String> updateStandings() {
        standingService.updateStandings();
        return ResponseEntity.ok("✅ Tournament standings updated successfully!");
    }

    // 📊 Просмотр всей таблицы
    @GetMapping
    public ResponseEntity<List<Standing>> getAllStandings() {
        return ResponseEntity.ok(standingService.getAllStandings());
    }
}
