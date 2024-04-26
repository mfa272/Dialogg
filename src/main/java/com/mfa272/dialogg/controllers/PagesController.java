package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountDTO;
import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.dto.ReplyDTO;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.PostService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String showHome(Model model) {
        return "home";
    }

    @GetMapping("/{username}")
    public String userProfile(@PathVariable String username, Model model, @RequestParam(defaultValue = "0") int page) {
        if (accountService.UserExists(username)) {
            Page<PostDTO> posts = postService.getPostsByUser(username, page, 10);
            model.addAttribute("newReply", new ReplyDTO());
            model.addAttribute("username", username);
            model.addAttribute("newPost", new PostDTO());
            model.addAttribute("posts", posts);
            Page<AccountDTO> followers = accountService.getFollowers(username, 0, 5);
            model.addAttribute("followers", followers);
            Page<AccountDTO> followed = accountService.getFollowing(username, 0, 5);
            model.addAttribute("followed", followed);

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
        Page<ReplyDTO> replies = postService.getRepliesByPost(postId, page, 10);
        model.addAttribute("post", post.get());
        model.addAttribute("newReply", new ReplyDTO());
        model.addAttribute("replies", replies);

        if (replyId != null) {
            Page<ReplyDTO> repliesToReply = postService.getRepliesByReply(replyId, subPage, 10);
            model.addAttribute("repliesToReply", repliesToReply);
            model.addAttribute("replyId", replyId);
        }

        return "thread";
    }
}
