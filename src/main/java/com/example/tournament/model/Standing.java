package com.example.tournament.model;

import jakarta.persistence.*;

@Entity
@Table(name = "standing")
public class Standing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "games_played")
    private Integer gamesPlayed;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "draws")
    private Integer draws;

    @Column(name = "losses")
    private Integer losses;

    @Column(name = "goals_for")
    private Integer goalsFor;

    @Column(name = "goals_against")
    private Integer goalsAgainst;

    @Column(name = "points")
    private Integer points;


    public Standing() {}

    public Standing(Team team) {
        this.team = team;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.points = 0;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public Integer getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(Integer gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public Integer getWins() { return wins; }
    public void setWins(Integer wins) { this.wins = wins; }

    public Integer getDraws() { return draws; }
    public void setDraws(Integer draws) { this.draws = draws; }

    public Integer getLosses() { return losses; }
    public void setLosses(Integer losses) { this.losses = losses; }

    public Integer getGoalsFor() { return goalsFor; }
    public void setGoalsFor(Integer goalsFor) { this.goalsFor = goalsFor; }

    public Integer getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(Integer goalsAgainst) { this.goalsAgainst = goalsAgainst; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    @Override
    public String toString() {
        return "Standing{" +
                "id=" + id +
                ", team=" + team.getName() +
                ", gamesPlayed=" + gamesPlayed +
                ", wins=" + wins +
                ", draws=" + draws +
                ", losses=" + losses +
                ", goalsFor=" + goalsFor +
                ", goalsAgainst=" + goalsAgainst +
                ", points=" + points +
                '}';
    }
}
