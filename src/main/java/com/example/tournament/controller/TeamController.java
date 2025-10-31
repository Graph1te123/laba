package com.example.tournament.controller;

import com.example.tournament.model.Team;
import com.example.tournament.repository.TeamRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PostMapping
    public Team createTeam(@RequestBody Team team) {
        return teamRepository.save(team);
    }

    @GetMapping
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/{id}")
    public Team getTeam(@PathVariable Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Team updateTeam(@PathVariable Long id, @RequestBody Team updated) {
        return teamRepository.findById(id)
                .map(team -> {
                    team.setName(updated.getName());
                    team.setCity(updated.getCity());
                    return teamRepository.save(team);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamRepository.deleteById(id);
    }
}
