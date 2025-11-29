package com.example.tournament.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "home_team_id", referencedColumnName = "id", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", referencedColumnName = "id", nullable = false)
    private Team awayTeam;

    @ManyToOne
    @JoinColumn(name = "venue_id", referencedColumnName = "id", nullable = false)
    private Venue venue;

    @Column(name = "match_date", nullable = false)
    private LocalDateTime matchDate;

    @Column(name = "score_home")
    private Integer scoreHome;

    @Column(name = "score_away")
    private Integer scoreAway;


    public Match() {}

    public Match(Team homeTeam, Team awayTeam, Venue venue, LocalDateTime matchDate) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.venue = venue;
        this.matchDate = matchDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public Integer getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(Integer scoreHome) {
        this.scoreHome = scoreHome;
    }

    public Integer getScoreAway() {
        return scoreAway;
    }

    public void setScoreAway(Integer scoreAway) {
        this.scoreAway = scoreAway;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", homeTeam=" + homeTeam.getName() +
                ", awayTeam=" + awayTeam.getName() +
                ", venue=" + venue.getName() +
                ", matchDate=" + matchDate +
                ", scoreHome=" + scoreHome +
                ", scoreAway=" + scoreAway +
                '}';
    }
}
