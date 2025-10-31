package com.example.tournament.service;

import com.example.tournament.dto.MatchRequest;
import com.example.tournament.model.Match;
import com.example.tournament.model.Team;
import com.example.tournament.model.Venue;
import com.example.tournament.repository.MatchRepository;
import com.example.tournament.repository.TeamRepository;
import com.example.tournament.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final VenueRepository venueRepository;

    public MatchService(MatchRepository matchRepository,
                        TeamRepository teamRepository,
                        VenueRepository venueRepository) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.venueRepository = venueRepository;
    }

    // ✅ Создание матча
    public Match createMatch(MatchRequest request) {
        Optional<Team> homeTeamOpt = teamRepository.findById(request.getHomeTeamId());
        Optional<Team> awayTeamOpt = teamRepository.findById(request.getAwayTeamId());
        Optional<Venue> venueOpt = venueRepository.findById(request.getVenueId());

        if (homeTeamOpt.isEmpty() || awayTeamOpt.isEmpty() || venueOpt.isEmpty()) {
            throw new IllegalArgumentException("Одна из команд или место проведения не найдены");
        }

        Team homeTeam = homeTeamOpt.get();
        Team awayTeam = awayTeamOpt.get();
        Venue venue = venueOpt.get();
        LocalDateTime matchDate = request.getMatchDate();

        // Проверка: у команд нет других матчей в это время
        if (matchRepository.existsByHomeTeamOrAwayTeamAndMatchDate(homeTeam, awayTeam, matchDate)) {
            throw new IllegalStateException("Одна из команд уже играет в это время!");
        }

        Match match = new Match();
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setVenue(venue);
        match.setMatchDate(matchDate);
        match.setScoreHome(request.getScoreHome());
        match.setScoreAway(request.getScoreAway());

        return matchRepository.save(match);
    }

    // ✅ Получение всех матчей
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    // ✅ Получение одного матча по ID
    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Матч с ID " + id + " не найден"));
    }

    // ✅ Удаление матча
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    // ✅ Получить все матчи конкретной команды
    public List<Match> getMatchesByTeam(Long teamId) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getHomeTeam().getId().equals(teamId) || m.getAwayTeam().getId().equals(teamId))
                .toList();
    }

    // ✅ Получить все предстоящие матчи
    public List<Match> getUpcomingMatches() {
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findAll().stream()
                .filter(m -> m.getMatchDate().isAfter(now))
                .toList();
    }
}
