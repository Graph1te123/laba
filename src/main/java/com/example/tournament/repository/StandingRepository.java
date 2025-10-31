package com.example.tournament.repository;

import com.example.tournament.model.Standing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandingRepository extends JpaRepository<Standing, Long> {
}
