package com.example.tournament.service;

import com.example.tournament.model.Standing;
import com.example.tournament.model.Team;
import com.example.tournament.repository.StandingRepository;
import com.example.tournament.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StandingService {

    @Autowired
    private StandingRepository standingRepository;

    @Autowired
    private TeamRepository teamRepository;


    public void updateStandings() {
        List<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            Standing standing = standingRepository.findByTeamId(team.getId())
                    .orElseGet(() -> {
                        Standing newStanding = new Standing();
                        newStanding.setTeam(team);
                        return newStanding;
                    });


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
    }


    public List<Standing> getTopTeams(int limit) {
        return standingRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getPoints().compareTo(a.getPoints()))
                .limit(limit)
                .collect(Collectors.toList());
    }


    public void updateStandingAfterMatch(Standing homeStanding, Standing awayStanding,
                                         int homeGoals, int awayGoals) {

        homeStanding.setGamesPlayed(homeStanding.getGamesPlayed() + 1);
        awayStanding.setGamesPlayed(awayStanding.getGamesPlayed() + 1);


        homeStanding.setGoalsFor(homeStanding.getGoalsFor() + homeGoals);
        homeStanding.setGoalsAgainst(homeStanding.getGoalsAgainst() + awayGoals);

        awayStanding.setGoalsFor(awayStanding.getGoalsFor() + awayGoals);
        awayStanding.setGoalsAgainst(awayStanding.getGoalsAgainst() + homeGoals);


        if (homeGoals > awayGoals) {

            homeStanding.setWins(homeStanding.getWins() + 1);
            homeStanding.setPoints(homeStanding.getPoints() + 3);

            awayStanding.setLosses(awayStanding.getLosses() + 1);
        } else if (awayGoals > homeGoals) {

            awayStanding.setWins(awayStanding.getWins() + 1);
            awayStanding.setPoints(awayStanding.getPoints() + 3);

            homeStanding.setLosses(homeStanding.getLosses() + 1);
        } else {

            homeStanding.setDraws(homeStanding.getDraws() + 1);
            homeStanding.setPoints(homeStanding.getPoints() + 1);

            awayStanding.setDraws(awayStanding.getDraws() + 1);
            awayStanding.setPoints(awayStanding.getPoints() + 1);
        }


        standingRepository.save(homeStanding);
        standingRepository.save(awayStanding);
    }


    public List<Standing> getAllStandingsSorted() {
        return standingRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getPoints().compareTo(a.getPoints()))
                .collect(Collectors.toList());
    }


    public Standing getStandingByTeam(Long teamId) {
        return standingRepository.findByTeamId(teamId)
                .orElse(null);
    }


    public void resetAllStandings() {
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
    }
}
