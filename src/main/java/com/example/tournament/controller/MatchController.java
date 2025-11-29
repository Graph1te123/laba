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

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody MatchRequest request) {
        Match match = matchService.createMatch(request);
        return ResponseEntity.ok(match);
    }


    @GetMapping
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return ResponseEntity.ok(match);
    }


    @GetMapping("/upcoming")
    public List<Match> getUpcomingMatches() {
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findAll()
                .stream()
                .filter(match -> match.getMatchDate().isAfter(now))
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }


    @PutMapping("/{id}")
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


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        matchRepository.delete(match);
        return ResponseEntity.noContent().build();
    }
}
