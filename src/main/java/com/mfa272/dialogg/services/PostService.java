package com.mfa272.dialogg.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.dto.ReplyDTO;
import com.mfa272.dialogg.entities.Account;
import com.mfa272.dialogg.entities.Post;
import com.mfa272.dialogg.repositories.PostRepository;
import com.mfa272.dialogg.repositories.AccountRepository;
import com.mfa272.dialogg.entities.Reply;
import com.mfa272.dialogg.repositories.ReplyRepository;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AccountRepository userRepository;
    private final ReplyRepository replyRepository;

    @Autowired
    public PostService(PostRepository postRepository, AccountRepository userRepository,
            ReplyRepository replyRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.replyRepository = replyRepository;
    }

    public Optional<PostDTO> getPostById(Long postId) {
        return postRepository.findById(postId).map(p -> {
            PostDTO dto = new PostDTO();
            dto.setId(p.getId());
            dto.setContent(p.getContent());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUsername(p.getAccount().getUsername());
            dto.setRepliesCount(replyRepository.countRepliesByPost(p));
            return dto;
        });
    }

    public boolean createPost(PostDTO postDTO, String username) {
        Post post = new Post(postDTO.getContent(), userRepository.findByUsername(username).get());
        postRepository.save(post);
        return true;
    }

    public Page<PostDTO> getPostsByUser(String username, int page, int size) {
        Page<Post> posts = postRepository.findByAccountUsernameOrderByCreatedAtDesc(username,
                PageRequest.of(page, size));
        return posts.map(p -> {
            PostDTO dto = new PostDTO();
            dto.setId(p.getId());
            dto.setContent(p.getContent());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUsername(p.getAccount().getUsername());
            dto.setRepliesCount(replyRepository.countRepliesByPost(p));
            dto.setReplies(null);
            return dto;
        });
    }

    public Page<ReplyDTO> getRepliesByPost(Long postId, int page, int size) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            Page<Reply> replies = replyRepository.findByParentPostAndParentReplyIsNullOrderByCreatedAtDesc(
                    post, PageRequest.of(page, size));
            return replies.map(r -> {
                ReplyDTO rdto = new ReplyDTO();
                rdto.setId(r.getId());
                rdto.setContent(r.getContent());
                rdto.setCreatedAt(r.getCreatedAt());
                rdto.setUsername(r.getAccount().getUsername());
                rdto.setRepliesCount(replyRepository.countRepliesByReply(r));
                return rdto;
            });
        } else {
            return Page.empty();
        }
    }

    public Page<ReplyDTO> getRepliesByReply(Long replyId, int page, int size) {
        Page<Reply> replies = replyRepository.findByParentReplyOrderByCreatedAtDesc(
                replyRepository.findById(replyId).get(), PageRequest.of(page, size));
        return replies.map(r -> {
            ReplyDTO rdto = new ReplyDTO();
            rdto.setId(r.getId());
            rdto.setContent(r.getContent());
            rdto.setCreatedAt(r.getCreatedAt());
            rdto.setUsername(r.getAccount().getUsername());
            rdto.setRepliesCount(replyRepository.countRepliesByReply(r));
            return rdto;
        });
    }

    public boolean replyToPost(ReplyDTO replyDTO, String username, Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<Account> account = userRepository.findByUsername(username);

        if (post.isPresent() && account.isPresent()) {
            Reply reply = new Reply(replyDTO.getContent(), account.get(), post.get());
            replyRepository.save(reply);
            return true;
        }
        return false;
    }

    public boolean replyToReply(ReplyDTO replyDTO, String username, Long postId, Long replyId) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<Reply> toReply = replyRepository.findById(replyId);
        Optional<Account> account = userRepository.findByUsername(username);

        if (!(post.isPresent() || toReply.isPresent() || account.isPresent())) {
            return false;
        }

        Reply toReplyGot = toReply.get();

        if (toReplyGot.getParentPost().getId() != postId || toReplyGot.hasParentReply()) {
            return false;
        }

        Reply reply = new Reply(replyDTO.getContent(), account.get(), post.get(), toReplyGot);
        replyRepository.save(reply);
        return true;
    }
}
