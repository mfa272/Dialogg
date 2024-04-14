package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.UserRegistrationDTO;
import com.mfa272.dialogg.services.UserService;
import com.mfa272.dialogg.services.UserService.RegistrationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;


@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model){
        return "login";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDTO userDto,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        RegistrationResult registered = userService.registerUser(userDto);
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
}