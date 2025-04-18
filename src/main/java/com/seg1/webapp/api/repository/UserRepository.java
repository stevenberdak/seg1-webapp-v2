package com.seg1.webapp.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.seg1.webapp.api.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}