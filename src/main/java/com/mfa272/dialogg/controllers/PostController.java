package com.mfa272.dialogg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.services.PostService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts/new")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new PostDTO());
        return "createPost";
    }

    @PostMapping("/posts/new")
    public String createPost(@ModelAttribute("post") @Valid PostDTO postDTO, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "createPost";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        postService.createPost(postDTO, currentUsername);
        return "redirect:/" + currentUsername;
    }

    @GetMapping("/posts")
    public String showPosts(Model model){
        return "posts";
    }
}