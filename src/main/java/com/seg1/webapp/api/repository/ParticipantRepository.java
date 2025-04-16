package com.seg1.webapp.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seg1.webapp.api.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
