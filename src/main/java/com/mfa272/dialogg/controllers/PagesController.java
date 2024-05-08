package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountDTO;
import com.mfa272.dialogg.dto.FeedResponse;
import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.PostService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagesController {

    private AccountService accountService;
    private PostService postService;

    @Autowired
    public PagesController(AccountService userService, PostService postService) {
        this.accountService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String showHome(Model model,
            @RequestParam(required = false) boolean next,
            HttpSession session, HttpServletResponse response,
            @RequestHeader(value = "Referer", required = false) String referer) {

        model.addAttribute("newPost", new PostDTO());

        LocalDateTime sessionFollowedDate = null;
        LocalDateTime sessionNotFollowedDate = null;
        if (next) {
            sessionFollowedDate = (LocalDateTime) session.getAttribute("feedFollowedDate");
            sessionNotFollowedDate = (LocalDateTime) session.getAttribute("feedOtherDate");
        }

        Optional<String> username = accountService.getCurrentUsername();
        if (username.isPresent()) {
            FeedResponse fr = postService.getUserFeed(username.get(), sessionFollowedDate, sessionNotFollowedDate);
            model.addAttribute("posts", fr.getPosts());
            session.setAttribute("feedFollowedDate", fr.getLastFollowedDate());
            session.setAttribute("feedOtherDate", fr.getLastOtherDate());
        } else {
            Page<PostDTO> posts;
            if (sessionNotFollowedDate != null) {
                posts = postService.getPostsBeforeDate(sessionNotFollowedDate, 10);
                if (!posts.hasContent()) {
                    posts = postService.getPosts(0, 10);
                }
            } else {
                posts = postService.getPosts(0, 10);
            }
            if (!posts.isEmpty()) {
                PostDTO lastPost = posts.getContent().get(posts.getContent().size() - 1);
                session.setAttribute("feedOtherDate", lastPost.getCreatedAt());
            }
            model.addAttribute("posts", posts);
        }
        model.addAttribute("newReply", new PostDTO());
        model.addAttribute("newPost", new PostDTO());
        return "home";
    }

    @GetMapping("/{username}/profile")
    public String userProfile(@PathVariable String username, Model model, @RequestParam(defaultValue = "0") int page) {
        if (accountService.UserExists(username)) {
            Page<PostDTO> posts = postService.getPostsByUser(username, page, 10);
            model.addAttribute("newReply", new PostDTO());
            model.addAttribute("username", username);
            model.addAttribute("newPost", new PostDTO());
            model.addAttribute("posts", posts);
            Page<AccountDTO> followers = accountService.getFollowers(username, 0, 5);
            model.addAttribute("followers", followers);
            Page<AccountDTO> followed = accountService.getFollowing(username, 0, 5);
            model.addAttribute("followed", followed);
            long followersCount = accountService.countFollowers(username);
            model.addAttribute("followersCount", followersCount);
            long followingCount = accountService.countFollowing(username);
            model.addAttribute("followingCount", followingCount);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUsername = authentication.getName();
                model.addAttribute("following", accountService.isFollowing(currentUsername, username));
            }
            return "profile";
        }
        return "redirect:/";
    }

    @GetMapping("/{username}/followers")
    public String userFollowers(@PathVariable String username, Model model,
            @RequestParam(defaultValue = "0") int page) {
        if (accountService.UserExists(username)) {
            Page<AccountDTO> followers = accountService.getFollowers(username, page, 10);
            model.addAttribute("followers", followers);
            long count = accountService.countFollowers(username);
            model.addAttribute("count", count);
            return "followers";
        }
        return "/";
    }

    @GetMapping("/{username}/followed")
    public String userFollowing(@PathVariable String username, Model model,
            @RequestParam(defaultValue = "0") int page) {
        if (accountService.UserExists(username)) {
            Page<AccountDTO> following = accountService.getFollowing(username, page, 10);
            model.addAttribute("followed", following);
            long count = accountService.countFollowing(username);
            model.addAttribute("count", count);
            return "followed";
        }
        return "/";
    }

    @GetMapping("/thread/{postId}")
    public String getThread(@PathVariable Long postId,
            @RequestParam(required = false) Long replyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int subPage,
            Model model) {
        Optional<PostDTO> post = postService.getPostById(postId);
        Page<PostDTO> replies = postService.getRepliesByPost(postId, page, 10);
        model.addAttribute("post", post.get());
        model.addAttribute("newPost", new PostDTO());
        model.addAttribute("newReply", new PostDTO());
        model.addAttribute("replies", replies);

        if (replyId != null) {
            Page<PostDTO> repliesToReply = postService.getRepliesByReply(replyId, subPage, 10);
            model.addAttribute("repliesToReply", repliesToReply);
            model.addAttribute("replyId", replyId);
        }

        return "thread";
    }
}