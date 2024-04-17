package com.mfa272.dialogg.repositories;

import com.mfa272.dialogg.entities.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>{
    Page<Post> findByAccountUsernameOrderByCreatedAtDesc(String Username, Pageable pageable);

}
