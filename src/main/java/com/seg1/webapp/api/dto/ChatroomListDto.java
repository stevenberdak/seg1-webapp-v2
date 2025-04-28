package com.seg1.webapp.api.dto;

public class ChatroomListDto {
    private Long id;
    private String title;
    private String header;
    private String description;

    public ChatroomListDto() {}

    public ChatroomListDto(Long id, String title, String header, String description) {
        this.id = id;
        this.title = title;
        this.header = header;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}