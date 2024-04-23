package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountDTO;
import com.mfa272.dialogg.dto.AccountDTO.RegistrationForm;
import com.mfa272.dialogg.dto.AccountDTO.UpdateEmailForm;
import com.mfa272.dialogg.dto.AccountDTO.UpdatePasswordForm;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.AccountService.OperationResult;
import com.mfa272.dialogg.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthenticationController {

    private AccountService accountService;

    @Autowired
    public AuthenticationController(AccountService userService, PostService postService) {
        this.accountService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }
    
    @GetMapping("/failedLogin")
    public String showFailedLoginPage(Model model) {
        model.addAttribute("errorMessage", "Wrong credentials");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AccountDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") @Validated({RegistrationForm.class}) AccountDTO userDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        OperationResult registered = accountService.registerUser(userDto);
        if (registered == OperationResult.USERNAME_TAKEN) {
            result.rejectValue("username", "user.username", "Username already taken");
            return "register";
        }
        if (registered == OperationResult.EMAIL_TAKEN) {
            result.rejectValue("email", "user.email", "Email already taken");
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "You have successfully registered!");
        return "redirect:/login";
    }

    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        Optional<String> username = accountService.getCurrentUsername();
        AccountDTO dto = new AccountDTO();
        dto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
        model.addAttribute("user", dto);
        return "settings";
    }
    
    @PostMapping("/updateEmail")
    public String updateEmail(@ModelAttribute("user") @Validated({UpdateEmailForm.class}) AccountDTO accountDto, BindingResult result, RedirectAttributes redirectAttributes) {
        Optional<String> username = accountService.getCurrentUsername();
        if (result.hasErrors()) {
            accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
            return "settings";
        }
        OperationResult r = accountService.updateEmail(username.get(), accountDto.getEmail());
        if (r == OperationResult.EMAIL_TAKEN){
            accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
            result.rejectValue("email", "user.email", "Email already taken");
            return "settings";
        }else if (r == OperationResult.FAILURE){
            accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
            result.rejectValue("email", "user.email", "Error");
            return "settings";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Email updated successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@ModelAttribute("user") @Validated({UpdatePasswordForm.class}) AccountDTO accountDto, BindingResult result, RedirectAttributes redirectAttributes) {
        Optional<String> username = accountService.getCurrentUsername();
        if (result.hasErrors()) {
            accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
            return "settings";
        }
        accountService.updatePassword(username.get(), accountDto.getPassword());
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(RedirectAttributes redirectAttributes){
        Optional<String> username = accountService.getCurrentUsername();
        accountService.deleteAccount(username.get());
        redirectAttributes.addFlashAttribute("successMessage", "Account deleted succesfully!");
        return "redirect:/login";
    }
}
