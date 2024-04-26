package com.mfa272.dialogg.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "replies")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post parentPost;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Reply parentReply;

    public Reply() {
    }

    public Reply(String content, Account account, Post parentPost) {
        this.content = content;
        this.account = account;
        this.parentPost = parentPost;
        this.createdAt = LocalDateTime.now();
    }

    public Reply(String content, Account account, Post parentPost, Reply parentReply) {
        this(content, account, parentPost);
        this.parentReply = parentReply;
    }

    public Reply getParentReply() {
        return parentReply;
    }

    public void setParentReply(Reply parentReply) {
        this.parentReply = parentReply;
    }

    public boolean hasParentReply() {
        return getParentReply() != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Post getParentPost() {
        return parentPost;
    }

    public void setParentPost(Post post) {
        this.parentPost = post;
    }
}
