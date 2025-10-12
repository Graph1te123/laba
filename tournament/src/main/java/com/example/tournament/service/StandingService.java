package com.example.tournament.service;

import com.example.tournament.model.Match;
import com.example.tournament.model.Standing;
import com.example.tournament.model.Team;
import com.example.tournament.repository.MatchRepository;
import com.example.tournament.repository.StandingRepository;
import com.example.tournament.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StandingService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final StandingRepository standingRepository;

    public StandingService(MatchRepository matchRepository, TeamRepository teamRepository, StandingRepository standingRepository) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.standingRepository = standingRepository;
    }

    // 📈 Пересчитать турнирную таблицу заново
    public void updateStandings() {
        List<Team> teams = teamRepository.findAll();
        List<Match> matches = matchRepository.findAll();

        // Временное хранилище статистики
        Map<Long, Standing> standingsMap = new HashMap<>();

        // Создаём запись для каждой команды
        for (Team team : teams) {
            Standing standing = new Standing();
            standing.setTeam(team);
            standing.setPlayed(0);
            standing.setWins(0);
            standing.setDraws(0);
            standing.setLosses(0);
            standing.setGoalsFor(0);
            standing.setGoalsAgainst(0);
            standing.setPoints(0);
            standingsMap.put(team.getId(), standing);
        }

        // 🏟️ Перебираем все матчи и обновляем статистику
        for (Match match : matches) {
            if (match.getScoreHome() == null || match.getScoreAway() == null) continue;

            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();
            int homeScore = match.getScoreHome();
            int awayScore = match.getScoreAway();

            Standing homeStanding = standingsMap.get(home.getId());
            Standing awayStanding = standingsMap.get(away.getId());

            // обновляем сыгранные матчи
            homeStanding.setPlayed(homeStanding.getPlayed() + 1);
            awayStanding.setPlayed(awayStanding.getPlayed() + 1);

            // обновляем голы
            homeStanding.setGoalsFor(homeStanding.getGoalsFor() + homeScore);
            homeStanding.setGoalsAgainst(homeStanding.getGoalsAgainst() + awayScore);
            awayStanding.setGoalsFor(awayStanding.getGoalsFor() + awayScore);
            awayStanding.setGoalsAgainst(awayStanding.getGoalsAgainst() + homeScore);

            // победа / ничья / поражение
            if (homeScore > awayScore) {
                homeStanding.setWins(homeStanding.getWins() + 1);
                homeStanding.setPoints(homeStanding.getPoints() + 3);
                awayStanding.setLosses(awayStanding.getLosses() + 1);
            } else if (awayScore > homeScore) {
                awayStanding.setWins(awayStanding.getWins() + 1);
                awayStanding.setPoints(awayStanding.getPoints() + 3);
                homeStanding.setLosses(homeStanding.getLosses() + 1);
            } else {
                homeStanding.setDraws(homeStanding.getDraws() + 1);
                awayStanding.setDraws(awayStanding.getDraws() + 1);
                homeStanding.setPoints(homeStanding.getPoints() + 1);
                awayStanding.setPoints(awayStanding.getPoints() + 1);
            }
        }

        // 💾 Сохраняем таблицу в базу
        standingRepository.deleteAll();
        standingRepository.saveAll(standingsMap.values());
    }

    // 📋 Получение таблицы
    public List<Standing> getAllStandings() {
        return standingRepository.findAll();
    }
}
