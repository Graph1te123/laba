package com.example.tournament.repository;

import com.example.tournament.model.Standing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StandingRepository extends JpaRepository<Standing, Long> {
    Optional<Standing> findByTeamId(Long teamId);
}
