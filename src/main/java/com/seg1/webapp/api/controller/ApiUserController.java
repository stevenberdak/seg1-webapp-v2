package com.seg1.webapp.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import com.seg1.webapp.api.entity.User;
import com.seg1.webapp.api.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {
    private final UserRepository repo;

    public ApiUserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return repo.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}