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
            RedirectAttributes redirectAttributes, @RequestHeader(value = "Referer", required = false) String referer) {
        Optional<String> currentUsername = accountService.getCurrentUsername();
        if (!result.hasErrors()) {
            postService.createPost(postDTO, currentUsername.get());
        } else {
            redirectAttributes.addFlashAttribute("postError", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:" + (referer != null ? referer : "/");
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
    public String postReply(@ModelAttribute("newReply") @Valid PostDTO replyDTO, BindingResult result,
            RedirectAttributes redirectAttributes,
            @RequestParam Long postId,
            @RequestParam(required = false) Long replyId,
            @RequestHeader(value = "Referer", required = false) String referer) {
        String username = accountService.getCurrentUsername().get();
        boolean success;

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("postError", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:" + (referer != null ? referer : "/");
        }

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
        redirectAttributes.addFlashAttribute("postError", "Error");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/like")
    public String like(@RequestParam(required = false) Long postId, @RequestParam(required = false) Long replyId,
            @RequestParam(required = false) Long subreplyId,
            RedirectAttributes redirectAttributes) {
        if (replyId != null && subreplyId != null) {
            accountService.likeReply(subreplyId);
            return "redirect:/thread/" + postId + "?replyId=" + String.valueOf(replyId);
        } else if (replyId != null) {
            accountService.likeReply(replyId);
            return "redirect:/thread/" + postId + "?replyId=" + String.valueOf(replyId);
        } else if (postId != null) {
            accountService.likePost(postId);
            return "redirect:/thread/" + postId;
        }
        return "redirect:/";
    }

    @PostMapping("/unlike")
    public String unlike(@RequestParam(required = false) Long postId, @RequestParam(required = false) Long replyId,
            @RequestParam(required = false) Long subreplyId,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
        if (replyId != null && subreplyId != null) {
            accountService.unlikeReply(subreplyId);
            return "redirect:/thread/" + postId + "?replyId=" + String.valueOf(replyId);
        } else if (replyId != null) {
            accountService.unlikeReply(replyId);
            return "redirect:/thread/" + postId + "?replyId=" + String.valueOf(replyId);
        } else if (postId != null) {
            accountService.unlikePost(postId);
            return "redirect:/thread/" + postId;
        }
        return "redirect:/";
    }
}
