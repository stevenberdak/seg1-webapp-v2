package com.seg1.webapp.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.seg1.webapp.api.entity.User;
import com.seg1.webapp.api.repository.UserRepository;

@Controller
public class SignUpController {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/sign-up-submit")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/sign-up.html?error=Username+already+exists";
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        userRepository.save(user);

        return "redirect:/sign-in.html";
    }
}