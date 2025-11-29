package com.example.tournament.controller;

import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.model.Venue;
import com.example.tournament.repository.VenueRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueRepository venueRepository;

    public VenueController(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        return ResponseEntity.ok(venueRepository.save(venue));
    }

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenue(@PathVariable Long id) {
        return ResponseEntity.ok(
                venueRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Venue with ID " + id + " not found"))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVenue(@PathVariable Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venue with ID " + id + " not found");
        }
        venueRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Venue deleted successfully"));
    }
}