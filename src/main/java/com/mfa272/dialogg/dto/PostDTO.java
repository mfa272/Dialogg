package com.mfa272.dialogg.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostDTO {

    @Size(max = 140, message = "No more than 140 characters allowed")
    @NotBlank(message = "Post can't be blank")
    private String content;

    private String username;

    private LocalDateTime createdAt;

    public String getCreatedAt() {
        return createdAt.toString();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}