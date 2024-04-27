package com.mfa272.dialogg.repositories;

import com.mfa272.dialogg.entities.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long>{

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByAccountUsernameOrderByCreatedAtDesc(String Username, Pageable pageable);
    
    @Query("SELECT p FROM Post p JOIN p.account.followers f WHERE f.username = :username ORDER BY p.createdAt DESC")
    Page<Post> findPostsByFollowedAccounts(@Param("username") String username, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.account.id NOT IN (SELECT a.id FROM Account a JOIN a.followers f WHERE f.username = :username) ORDER BY p.createdAt DESC")
    Page<Post> findPostsByNotFollowedAccounts(@Param("username") String username, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.account.followers f WHERE f.username = :username AND p.createdAt < :date ORDER BY p.createdAt DESC")
    Page<Post> findPostsByFollowedAccountsBeforeDate(@Param("username") String username, @Param("date") LocalDateTime date, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.account.id NOT IN (SELECT a.id FROM Account a JOIN a.followers f WHERE f.username = :username) AND p.createdAt < :date ORDER BY p.createdAt DESC")
    Page<Post> findPostsByNotFollowedAccountsBeforeDate(@Param("username") String username, @Param("date") LocalDateTime date, Pageable pageable);

    Page<Post> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime date, Pageable pageable);
}
