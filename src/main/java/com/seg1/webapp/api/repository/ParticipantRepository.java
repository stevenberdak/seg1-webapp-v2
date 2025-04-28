package com.seg1.webapp.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seg1.webapp.api.entity.Participant;

import jakarta.transaction.Transactional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByChatroomId(Long chatroomId);

    @Transactional
    void deleteByUsernameAndChatroomId(String username, Long chatroomId);
}