package com.mfa272.dialogg.dto;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostDTO {

    @Size(max = 140, message = "No more than 140 characters allowed")
    @NotBlank(message = "Post can't be blank")
    private String content;

    private String username;

    private LocalDateTime createdAt;

    private Long id;

    private Page<PostDTO> replies;

    private Long repliesCount;

    private Long threadId;
    
    private Long likesCount;

    private boolean isLiked;

    public Long getThreadId() {
        return threadId;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
    
    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(Long repliesCount) {
        this.repliesCount = repliesCount;
    }

    public Page<PostDTO> getReplies() {
        return replies;
    }

    public void setReplies(Page<PostDTO> replies) {
        this.replies = replies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
