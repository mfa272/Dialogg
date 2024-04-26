package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.dto.ReplyDTO;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.PostService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class SocialController {

    private AccountService accountService;
    private PostService postService;

    @Autowired
    public SocialController(AccountService userService, PostService postService) {
        this.accountService = userService;
        this.postService = postService;
    }

    @PostMapping("/follow/{username}")
    public String followAccount(@PathVariable String username) {
        Optional<String> currentUsername = accountService.getCurrentUsername();
        accountService.follow(currentUsername.get(), username);
        return "redirect:/" + username;
    }

    @PostMapping("/unfollow/{username}")
    public String unfollowAccount(@PathVariable String username) {
        Optional<String> currentUsername = accountService.getCurrentUsername();
        accountService.unfollow(currentUsername.get(), username);
        return "redirect:/" + username;
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute("newPost") @Valid PostDTO postDTO, BindingResult result,
            RedirectAttributes redirectAttributes) {
        Optional<String> currentUsername = accountService.getCurrentUsername();
        if (!result.hasErrors()) {
            postService.createPost(postDTO, currentUsername.get());
        } else {
            redirectAttributes
                    .addFlashAttribute("content", result.getFieldError("content")
                            .getDefaultMessage());
        }
        return "redirect:/" + currentUsername.get();
    }

    @PostMapping("/reply")
    public String postReply(@ModelAttribute("newReply") ReplyDTO replyDTO, @RequestParam Long postId,
            @RequestParam(required = false) Long replyId) {
        String username = accountService.getCurrentUsername().get();
        boolean success;

        if (replyId != null) {
            success = postService.replyToReply(replyDTO, username, postId, replyId);
        } else {
            success = postService.replyToPost(replyDTO, username, postId);
        }

        if (success && replyId == null) {
            return "redirect:/thread/" + postId;
        } else if (success && replyId != null) {
            return "redirect:/thread/" + postId + "?replyId=" + String.valueOf(replyId);
        }
        return "redirect:/";
    }
}
