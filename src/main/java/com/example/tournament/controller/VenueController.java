package com.example.tournament.controller;

import com.example.tournament.model.Venue;
import com.example.tournament.repository.VenueRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueRepository venueRepository;

    public VenueController(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @PostMapping
    public Venue createVenue(@RequestBody Venue venue) {
        return venueRepository.save(venue);
    }

    @GetMapping
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    @GetMapping("/{id}")
    public Venue getVenue(@PathVariable Long id) {
        return venueRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteVenue(@PathVariable Long id) {
        venueRepository.deleteById(id);
    }
}
