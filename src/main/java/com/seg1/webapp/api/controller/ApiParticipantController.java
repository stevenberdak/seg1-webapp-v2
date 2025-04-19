package com.seg1.webapp.api.controller;

import com.seg1.webapp.api.entity.Participant;
import com.seg1.webapp.api.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ApiParticipantController {

    private final ParticipantRepository repo;

    @Autowired
    public ApiParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Participant> create(@RequestBody Participant participant) {
        Participant saved = repo.save(participant);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Participant> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participant not found.");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}