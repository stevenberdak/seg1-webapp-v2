package com.seg1.webapp.api.controller;

import org.hibernate.type.internal.UserTypeJavaTypeWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.seg1.webapp.api.repository.UserRepository;
import com.seg1.webapp.api.entity.User;

@Controller
public class SignUpController {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/sign-up-submit.html")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/sign-up.html?error=Email+already+in+use";
        }

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