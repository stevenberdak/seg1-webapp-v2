package com.seg1.webapp.api.dto;
import java.time.LocalDateTime;

public record MessageDto(String username, String content, LocalDateTime timestamp) {}