package com.mfa272.dialogg.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfa272.dialogg.dto.FeedResponse;
import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.entities.Account;
import com.mfa272.dialogg.entities.Post;
import com.mfa272.dialogg.repositories.PostRepository;
import com.mfa272.dialogg.repositories.AccountRepository;
import com.mfa272.dialogg.entities.Reply;
import com.mfa272.dialogg.repositories.ReplyRepository;

import java.time.LocalDateTime;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final ReplyRepository replyRepository;

    @Autowired
    public PostService(PostRepository postRepository, AccountRepository userRepository,
            ReplyRepository replyRepository) {
        this.postRepository = postRepository;
        this.accountRepository = userRepository;
        this.replyRepository = replyRepository;
    }

    public Page<PostDTO> getFollowedPostsBeforeDate(String username, LocalDateTime date, int page, int size) {
        Page<Post> posts = postRepository.findPostsByFollowedAccountsBeforeDate(username, date,
                PageRequest.of(page, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Page<PostDTO> getNotFollowedPostsBeforeDate(String username, LocalDateTime date, int page, int size) {
        Page<Post> posts = postRepository.findPostsByNotFollowedAccountsBeforeDate(username, date,
                PageRequest.of(page, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Page<PostDTO> getPostsBeforeDate(LocalDateTime date, int size) {
        Page<Post> posts = postRepository.findByCreatedAtBeforeOrderByCreatedAtDesc(date, PageRequest.of(0, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Page<PostDTO> getFollowedPosts(String username, int page, int size) {
        Page<Post> posts = postRepository.findPostsByFollowedAccounts(username, PageRequest.of(page, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Page<PostDTO> getNotFollowedPosts(String username, int page, int size) {
        Page<Post> posts = postRepository.findPostsByNotFollowedAccounts(username, PageRequest.of(page, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Page<PostDTO> getPosts(int page, int size) {
        Page<Post> posts = postRepository.findByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Optional<PostDTO> getPostById(Long postId) {
        return postRepository.findById(postId).map(p -> convertToPostDTO(p));
    }

    public boolean createPost(PostDTO postDTO, String username) {
        Post post = new Post(postDTO.getContent(), accountRepository.findByUsername(username).get());
        postRepository.save(post);
        return true;
    }

    public Page<PostDTO> getPostsByUser(String username, int page, int size) {
        Page<Post> posts = postRepository.findByAccountUsernameOrderByCreatedAtDesc(username,
                PageRequest.of(page, size));
        return posts.map(p -> convertToPostDTO(p));
    }

    public Page<PostDTO> getRepliesByPost(Long postId, int page, int size) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            Page<Reply> replies = replyRepository.findByParentPostAndParentReplyIsNullOrderByCreatedAtDesc(
                    post, PageRequest.of(page, size));
            return replies.map(r -> {
                PostDTO rdto = new PostDTO();
                rdto.setId(r.getId());
                rdto.setContent(r.getContent());
                rdto.setCreatedAt(r.getCreatedAt());
                rdto.setUsername(r.getAccount().getUsername());
                rdto.setRepliesCount(replyRepository.countRepliesByReply(r));
                rdto.setThreadId(postId);
                rdto.setLikesCount(replyRepository.countLikesByReply_Id(r.getId()));
                rdto.setLiked(replyRepository.isReplyLikedByUser(r.getId(),
                        SecurityContextHolder.getContext().getAuthentication().getName()));
                return rdto;
            });
        } else {
            return Page.empty();
        }
    }

    public Page<PostDTO> getRepliesByReply(Long replyId, int page, int size) {
        Page<Reply> replies = replyRepository.findByParentReply_IdOrderByCreatedAtDesc(
                replyId, PageRequest.of(page, size));
        return replies.map(r -> {
            PostDTO rdto = new PostDTO();
            rdto.setId(r.getId());
            rdto.setContent(r.getContent());
            rdto.setCreatedAt(r.getCreatedAt());
            rdto.setUsername(r.getAccount().getUsername());
            rdto.setRepliesCount(replyRepository.countRepliesByReply(r));
            rdto.setThreadId(r.getParentPost().getId());
            rdto.setLikesCount(replyRepository.countLikesByReply_Id(r.getId()));
            rdto.setLiked(replyRepository.isReplyLikedByUser(r.getId(),
                    SecurityContextHolder.getContext().getAuthentication().getName()));
            return rdto;
        });
    }

    public boolean replyToPost(PostDTO PostDTO, String username, Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<Account> account = accountRepository.findByUsername(username);

        if (post.isPresent() && account.isPresent()) {
            Reply reply = new Reply(PostDTO.getContent(), account.get(), post.get());
            replyRepository.save(reply);
            return true;
        }
        return false;
    }

    public boolean replyToReply(PostDTO PostDTO, String username, Long postId, Long replyId) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<Reply> toReply = replyRepository.findById(replyId);
        Optional<Account> account = accountRepository.findByUsername(username);

        if (!(post.isPresent() || toReply.isPresent() || account.isPresent())) {
            return false;
        }

        Reply toReplyGot = toReply.get();

        if (toReplyGot.getParentPost().getId() != postId || toReplyGot.hasParentReply()) {
            return false;
        }

        Reply reply = new Reply(PostDTO.getContent(), account.get(), post.get(), toReplyGot);
        replyRepository.save(reply);
        return true;
    }

    private PostDTO convertToPostDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUsername(post.getAccount().getUsername());
        dto.setRepliesCount(replyRepository.countRepliesByPost(post));
        dto.setLikesCount(postRepository.countLikesByPost_Id(post.getId()));
        dto.setLiked(postRepository.isPostLikedByUser(post.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName()));
        return dto;
    }

    @Transactional
    public boolean deletePost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (post.get().getAccount().getUsername().equals(currentUsername)) {
                postRepository.delete(post.get());
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean deleteReply(Long replyId) {
        Optional<Reply> reply = replyRepository.findById(replyId);
        if (reply.isPresent()) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (reply.get().getAccount().getUsername().equals(currentUsername)) {
                replyRepository.delete(reply.get());
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean likePost(Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Post> post = postRepository.findById(postId);
        Optional<Account> account = accountRepository.findByUsername(username);

        if (!(post.isPresent()) || !(account.isPresent())) {
            return false;
        }
        if (post.get().getAccount().getUsername().equals(username)) {
            return false;
        }
        Post p = post.get();
        p.likePost(account.get());
        return true;
    }

    @Transactional
    public boolean unlikePost(Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Post> post = postRepository.findById(postId);
        Optional<Account> account = accountRepository.findByUsername(username);

        if (!(post.isPresent()) || !(account.isPresent())) {
            return false;
        }
        if (post.get().getAccount().getUsername().equals(username)) {
            return false;
        }
        Post p = post.get();
        p.unlikePost(account.get());
        return true;
    }

    @Transactional
    public boolean likeReply(Long replyId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Reply> reply = replyRepository.findById(replyId);
        Optional<Account> account = accountRepository.findByUsername(username);

        if (!(reply.isPresent()) || !(account.isPresent())) {
            return false;
        }
        if (reply.get().getAccount().getUsername().equals(username)) {
            return false;
        }
        Reply p = reply.get();
        p.likeReply(account.get());
        return true;
    }

    @Transactional
    public boolean unlikeReply(Long replyId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Reply> reply = replyRepository.findById(replyId);
        Optional<Account> account = accountRepository.findByUsername(username);

        if (!(reply.isPresent()) || !(account.isPresent())) {
            return false;
        }
        if (reply.get().getAccount().getUsername().equals(username)) {
            return false;
        }
        Reply p = reply.get();
        p.unlikeReply(account.get());
        return true;
    }

    public FeedResponse getUserFeed(String username, LocalDateTime lastFollowedDate, LocalDateTime lastOtherDate) {
        Page<PostDTO> followedPosts;
        Page<PostDTO> otherPosts;
        ArrayList<PostDTO> mergedPosts = new ArrayList<>();
        Page<PostDTO> posts;

        if (lastFollowedDate == null) {
            followedPosts = getFollowedPosts(username, 0, 10);
        } else {
            followedPosts = getFollowedPostsBeforeDate(username, lastFollowedDate, 0, 10);
        }

        if (lastOtherDate == null) {
            otherPosts = getNotFollowedPosts(username, 0, 10);
        } else {
            otherPosts = getNotFollowedPostsBeforeDate(username, lastOtherDate, 0, 10);
        }

        ArrayList<PostDTO> followedContent = new ArrayList<>(followedPosts.getContent());
        ArrayList<PostDTO> notFollowedContent = new ArrayList<>(otherPosts.getContent());

        while (mergedPosts.size() < 10 && (!followedContent.isEmpty() || !notFollowedContent.isEmpty())) {
            for (int i = 0; i < 3 && !followedContent.isEmpty() && mergedPosts.size() < 10; i++) {
                PostDTO post = followedContent.remove(0);
                mergedPosts.add(post);
                lastFollowedDate = post.getCreatedAt();
            }
            for (int i = 0; i < 3 && !notFollowedContent.isEmpty() && mergedPosts.size() < 10; i++) {
                PostDTO post = notFollowedContent.remove(0);
                if (!post.getUsername().equals(username)) {
                    mergedPosts.add(post);
                    lastOtherDate = post.getCreatedAt();
                }
            }
        }

        boolean hasNext = followedPosts.getNumberOfElements() + otherPosts.getNumberOfElements() > 10
                || otherPosts.hasNext() || followedPosts.hasNext();

        int size = Math.max(mergedPosts.size(), 1);
        mergedPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));
        posts = new PageImpl<>(mergedPosts, PageRequest.of(0, size),
                hasNext ? 11 : mergedPosts.size());
        return new FeedResponse(posts, lastFollowedDate, lastOtherDate);
    }
}
