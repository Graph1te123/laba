package com.example.tournament.controller;

import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.model.Standing;
import com.example.tournament.model.Team;
import com.example.tournament.repository.StandingRepository;
import com.example.tournament.repository.TeamRepository;
import com.example.tournament.service.StandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
public class StandingController {

    @Autowired
    private StandingRepository standingRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StandingService standingService;


    @GetMapping
    public List<Standing> getAllStandings() {
        return standingRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Standing> getStandingById(@PathVariable Long id) {
        Standing standing = standingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Standing not found with id: " + id));
        return ResponseEntity.ok(standing);
    }


    @GetMapping("/team/{teamId}")
    public ResponseEntity<Standing> getStandingByTeam(@PathVariable Long teamId) {
        Standing standing = standingRepository.findByTeamId(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Standing not found for team id: " + teamId));
        return ResponseEntity.ok(standing);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Standing> updateStanding(@PathVariable Long id, @RequestBody Standing standingDetails) {
        Standing standing = standingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Standing not found with id: " + id));

        standing.setGamesPlayed(standingDetails.getGamesPlayed());
        standing.setWins(standingDetails.getWins());
        standing.setDraws(standingDetails.getDraws());
        standing.setLosses(standingDetails.getLosses());
        standing.setGoalsFor(standingDetails.getGoalsFor());
        standing.setGoalsAgainst(standingDetails.getGoalsAgainst());
        standing.setPoints(standingDetails.getPoints());

        Standing updated = standingRepository.save(standing);
        return ResponseEntity.ok(updated);
    }


    @PostMapping("/reset")
    public ResponseEntity<String> resetStandings() {
        standingRepository.deleteAll();


        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            Standing standing = new Standing();
            standing.setTeam(team);
            standing.setGamesPlayed(0);
            standing.setWins(0);
            standing.setDraws(0);
            standing.setLosses(0);
            standing.setGoalsFor(0);
            standing.setGoalsAgainst(0);
            standing.setPoints(0);
            standingRepository.save(standing);
        }

        return ResponseEntity.ok("Standings reset successfully! Created standings for " + teams.size() + " teams.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStanding(@PathVariable Long id) {
        Standing standing = standingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Standing not found with id: " + id));
        standingRepository.delete(standing);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping
    public ResponseEntity<String> deleteAllStandings() {
        long count = standingRepository.count();
        standingRepository.deleteAll();
        return ResponseEntity.ok("All standings deleted! Total deleted: " + count);
    }
}
