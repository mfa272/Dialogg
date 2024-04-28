package com.mfa272.dialogg.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mfa272.dialogg.entities.Post;
import com.mfa272.dialogg.entities.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    
    Page<Reply> findByParentPostAndParentReplyIsNullOrderByCreatedAtDesc(Post post, Pageable pageable);

    Page<Reply> findByParentReply_IdOrderByCreatedAtDesc(Long parentReplyId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reply r WHERE r.parentPost = :post")
    Long countRepliesByPost(@Param("post") Post post);

    @Query("SELECT COUNT(r) FROM Reply r WHERE r.parentReply = :reply")
    Long countRepliesByReply(@Param("reply") Reply reply);
}
