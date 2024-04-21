package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountRegistrationDTO;
import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.AccountService.RegistrationResult;
import com.mfa272.dialogg.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class AccountController {

    private AccountService accountService;
    private PostService postService;

    @Autowired
    public AccountController(AccountService userService, PostService postService) {
        this.accountService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String showHome(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AccountRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") @Valid AccountRegistrationDTO userDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        RegistrationResult registered = accountService.registerUser(userDto);
        if (registered == RegistrationResult.USERNAME_TAKEN) {
            result.rejectValue("username", "user.username", "This username is already taken");
            return "register";
        }
        if (registered == RegistrationResult.EMAIL_TAKEN) {
            result.rejectValue("email", "user.email", "An account already exists for this email");
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "You have successfully registered!");
        return "redirect:/login";
    }

    @GetMapping("/{username}")
    public String userProfile(@PathVariable String username, Model model, @RequestParam(defaultValue = "0") int page) {
        if (accountService.UserExists(username)) {
            Page<PostDTO> posts = postService.getPostsByUser(username, page, 10);
            model.addAttribute("username", username);
            model.addAttribute("newPost", new PostDTO());
            model.addAttribute("posts", posts);
            Page<AccountRegistrationDTO> followers = accountService.getFollowers(username, 0, 5);
            model.addAttribute("followers", followers);
            Page<AccountRegistrationDTO> followed = accountService.getFollowing(username, 0, 5);
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
            Page<AccountRegistrationDTO> followers = accountService.getFollowers(username, page, 10);
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
            Page<AccountRegistrationDTO> following = accountService.getFollowing(username, page, 10);
            model.addAttribute("followed", following);
            long count = accountService.countFollowing(username);
            model.addAttribute("count", count);
            return "followed";
        }
        return "/";
    }

    @PostMapping("/follow/{username}")
    public String followAccount(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        String currentUsername = authentication.getName();
        accountService.follow(currentUsername, username);
        return "redirect:/" + username;
    }

    @PostMapping("/unfollow/{username}")
    public String unfollowAccount(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        String currentUsername = authentication.getName();
        accountService.unfollow(currentUsername, username);
        return "redirect:/" + username;
    }
}
