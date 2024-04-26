package com.mfa272.dialogg.dto;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

public class ReplyDTO {

    private Long id;

    private String username;

    private LocalDateTime createdAt;

    private Page<ReplyDTO> replies;

    private Long repliesCount;

    private String content;

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(Long repliesCount) {
        this.repliesCount = repliesCount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Page<ReplyDTO> getReplies() {
        return replies;
    }

    public void setReplies(Page<ReplyDTO> replies) {
        this.replies = replies;
    }

}
