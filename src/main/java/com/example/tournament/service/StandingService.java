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

        Map<Long, Standing> standingsMap = new HashMap<>();

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

        for (Match match : matches) {
            if (match.getScoreHome() == null || match.getScoreAway() == null) continue;

            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();
            int homeScore = match.getScoreHome();
            int awayScore = match.getScoreAway();

            Standing homeStanding = standingsMap.get(home.getId());
            Standing awayStanding = standingsMap.get(away.getId());

            homeStanding.setPlayed(homeStanding.getPlayed() + 1);
            awayStanding.setPlayed(awayStanding.getPlayed() + 1);

            homeStanding.setGoalsFor(homeStanding.getGoalsFor() + homeScore);
            homeStanding.setGoalsAgainst(homeStanding.getGoalsAgainst() + awayScore);
            awayStanding.setGoalsFor(awayStanding.getGoalsFor() + awayScore);
            awayStanding.setGoalsAgainst(awayStanding.getGoalsAgainst() + homeScore);

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

        standingRepository.deleteAll();
        standingRepository.saveAll(standingsMap.values());
    }

    // 📋 Получение таблицы
    public List<Standing> getAllStandings() {
        return standingRepository.findAll();
    }

    // ✅ Получить топ команд по очкам
    public List<Standing> getTopTeams(int limit) {
        return standingRepository.findAll().stream()
                .sorted((a, b) -> {
                    int pointsCompare = Integer.compare(b.getPoints(), a.getPoints());
                    if (pointsCompare != 0) return pointsCompare;
                    int goalDiffA = a.getGoalsFor() - a.getGoalsAgainst();
                    int goalDiffB = b.getGoalsFor() - b.getGoalsAgainst();
                    return Integer.compare(goalDiffB, goalDiffA);
                })
                .limit(limit)
                .toList();
    }

    // ✅ Сбросить таблицу
    public void resetStandings() {
        List<Standing> standings = standingRepository.findAll();
        for (Standing s : standings) {
            s.setPlayed(0);
            s.setWins(0);
            s.setDraws(0);
            s.setLosses(0);
            s.setGoalsFor(0);
            s.setGoalsAgainst(0);
            s.setPoints(0);
        }
        standingRepository.saveAll(standings);
    }
}
