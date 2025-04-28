package com.seg1.webapp.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.seg1.webapp.api.entity.Message;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatroomId(Long chatroomId);
}
