package com.seg1.webapp.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seg1.webapp.api.entity.Participant;
import com.seg1.webapp.api.repository.ParticipantRepository;

@RestController
@RequestMapping("/api/participants")
public class ApiParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @GetMapping("/by-room/{roomId}")
    public List<String> getParticipantsByRoom(@PathVariable Long roomId) {
        List<Participant> participants = participantRepository.findByChatroomId(roomId);
        return participants.stream()
                .map(Participant::getUsername)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Participant> create(@RequestBody Participant participant) {
        Participant saved = participantRepository.save(participant);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Participant> getAll() {
        return participantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        return participantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!participantRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participant not found.");
        }
        participantRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}