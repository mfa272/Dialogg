package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountRegistrationDTO;
import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.entities.Account;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.AccountService.RegistrationResult;
import com.mfa272.dialogg.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public String showLoginPage(Model model){
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
        Account account = accountService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Page<PostDTO> posts = postService.getPostsByUser(username, page, 10);
        model.addAttribute("account", account);
        model.addAttribute("newPost", new PostDTO()); 
        model.addAttribute("posts", posts);
        return "profile";
    }
}
