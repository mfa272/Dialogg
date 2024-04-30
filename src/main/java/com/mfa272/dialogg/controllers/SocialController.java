package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.PostService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
        return "redirect:/" + username + "/profile";
    }

    @PostMapping("/unfollow/{username}")
    public String unfollowAccount(@PathVariable String username) {
        Optional<String> currentUsername = accountService.getCurrentUsername();
        accountService.unfollow(currentUsername.get(), username);
        return "redirect:/" + username + "/profile";
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
        return "redirect:/" + currentUsername.get() + "/profile";
    }

    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable Long postId, @RequestParam(required = false) Long replyId,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
        boolean deleted;
        if (replyId == null) {
            deleted = postService.deletePost(postId);
        } else {
            deleted = postService.deleteReply(replyId);
        }
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Deleted");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error");
        }
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/reply")
    public String postReply(@ModelAttribute("newReply") PostDTO replyDTO, @RequestParam Long postId,
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

    @PostMapping("/like")
    public String like(@RequestParam(required = false) Long postId, @RequestParam(required = false) Long replyId,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
        if (replyId == null) {
            postService.likePost(postId);
        } else if (postId == null) {
            postService.likeReply(replyId);
        }
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/unlike")
    public String unlike(@RequestParam(required = false) Long postId, @RequestParam(required = false) Long replyId,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
        if (replyId == null) {
            postService.unlikePost(postId);
        } else if (postId == null) {
            postService.unlikeReply(replyId);
        }
        return "redirect:" + (referer != null ? referer : "/");
    }
}
