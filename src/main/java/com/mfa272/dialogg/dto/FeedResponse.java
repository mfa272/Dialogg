package com.mfa272.dialogg.dto;

import org.springframework.data.domain.Page;
import java.time.LocalDateTime;

public class FeedResponse {
    private Page<PostDTO> posts;
    private LocalDateTime lastFollowedDate;
    private LocalDateTime lastGeneralDate;

    public FeedResponse(Page<PostDTO> posts, LocalDateTime lastFollowedDate, LocalDateTime lastOtherDate) {
        this.posts = posts;
        this.lastFollowedDate = lastFollowedDate;
        this.lastGeneralDate = lastOtherDate;
    }

    public Page<PostDTO> getPosts() {
        return posts;
    }
    public void setPosts(Page<PostDTO> posts) {
        this.posts = posts;
    }
    public LocalDateTime getLastFollowedDate() {
        return lastFollowedDate;
    }
    public void setLastFollowedDate(LocalDateTime lastFollowedDate) {
        this.lastFollowedDate = lastFollowedDate;
    }
    public LocalDateTime getLastOtherDate() {
        return lastGeneralDate;
    }
    public void setLastGeneralDate(LocalDateTime lastOtherDate) {
        this.lastGeneralDate = lastOtherDate;
    }
    
}