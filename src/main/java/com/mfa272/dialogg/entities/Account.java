package com.mfa272.dialogg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.Set;
import java.util.List;

import jakarta.persistence.CascadeType;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany()
    @JoinTable(name = "account_followers", joinColumns = @JoinColumn(name = "followed_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<Account> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<Account> following = new HashSet<>();

    public Account() {
        this.createdAt = LocalDateTime.now();
    }

    public Account(String username, String email, String password, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Account> getFollowers() {
        return followers;
    }

    public Set<Account> getFollowing() {
        return following;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addFollower(Account user) {
        this.followers.add(user);
    }

    public void removeFollower(Account user) {
        this.followers.remove(user);
    }

    public void addFollowing(Account user) {
        this.following.add(user);
    }

    public void removeFollowing(Account user) {
        this.following.remove(user);
    }
}
