package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountDTO;
import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.PostService;

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
        return "/";
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
}
