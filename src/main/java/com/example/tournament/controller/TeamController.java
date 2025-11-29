package com.example.tournament.controller;

import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.model.Team;
import com.example.tournament.repository.TeamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }


    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        return ResponseEntity.ok(teamRepository.save(team));
    }


    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(
                teamRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Team with ID " + id + " not found"))
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTeam(@PathVariable Long id, @RequestBody Team updated) {
        return ResponseEntity.ok(
                teamRepository.findById(id)
                        .map(team -> {
                            team.setName(updated.getName());
                            team.setCity(updated.getCity());
                            team.setCoach(updated.getCoach());
                            return teamRepository.save(team);
                        })
                        .orElseThrow(() -> new ResourceNotFoundException("Team with ID " + id + " not found"))
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTeam(@PathVariable Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team with ID " + id + " not found");
        }
        teamRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Team deleted successfully"));
    }
}