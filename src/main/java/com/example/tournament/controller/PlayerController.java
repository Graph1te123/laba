package com.example.tournament.controller;

import com.example.tournament.model.Player;
import com.example.tournament.model.Team;
import com.example.tournament.repository.PlayerRepository;
import com.example.tournament.repository.TeamRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerController(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @PostMapping("/team/{teamId}")
    public Player addPlayerToTeam(@PathVariable Long teamId, @RequestBody Player player) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        player.setTeam(team);
        return playerRepository.save(player);
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerRepository.deleteById(id);
    }
}
