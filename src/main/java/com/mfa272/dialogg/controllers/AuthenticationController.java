package com.mfa272.dialogg.controllers;

import com.mfa272.dialogg.dto.AccountDTO;
import com.mfa272.dialogg.dto.AccountDTO.RegistrationForm;
import com.mfa272.dialogg.dto.AccountDTO.UpdateEmailForm;
import com.mfa272.dialogg.dto.AccountDTO.UpdatePasswordForm;
import com.mfa272.dialogg.services.AccountService;
import com.mfa272.dialogg.services.AccountService.OperationResult;
import com.mfa272.dialogg.services.PostService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
        model.addAttribute("errorMessage", "Invalid credentials");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new AccountDTO());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") @Validated({ RegistrationForm.class }) AccountDTO userDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("user", userDto);
            return "redirect:/register";
        }
        OperationResult registered = accountService.registerUser(userDto);
        if (registered == OperationResult.USERNAME_TAKEN) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Username already taken");
            redirectAttributes.addFlashAttribute("user", userDto);
            return "redirect:/register";
        }
        if (registered == OperationResult.EMAIL_TAKEN) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Email already taken");
            redirectAttributes.addFlashAttribute("user", userDto);
            return "redirect:/register";
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
    public String updateEmail(@ModelAttribute("user") @Validated({ UpdateEmailForm.class }) AccountDTO accountDto,
            BindingResult result, RedirectAttributes redirectAttributes) {
        Optional<String> username = accountService.getCurrentUsername();
        if (result.hasErrors()) {
            accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
            redirectAttributes.addFlashAttribute("user", accountDto);
            redirectAttributes.addFlashAttribute("errorMessage", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/settings";
        }
        OperationResult r = accountService.updateEmail(username.get(), accountDto.getEmail());
        accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
        if (r == OperationResult.EMAIL_TAKEN) {
            redirectAttributes.addFlashAttribute("user", accountDto);
            redirectAttributes.addFlashAttribute("errorMessage", "Email already taken");
            return "redirect:/settings";
        } else if (r == OperationResult.FAILURE) {
            redirectAttributes.addFlashAttribute("user", accountDto);
            redirectAttributes.addFlashAttribute("errorMessage", "Error");
            return "redirect:/settings";
        }
        redirectAttributes.addFlashAttribute("user", accountDto);
        redirectAttributes.addFlashAttribute("successMessage", "Email updated successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@ModelAttribute("user") @Validated({ UpdatePasswordForm.class }) AccountDTO accountDto,
            BindingResult result, RedirectAttributes redirectAttributes) {
        Optional<String> username = accountService.getCurrentUsername();
        accountDto.setCurrentEmail(accountService.getEmailByUsername(username.get()));
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("user", accountDto);
            redirectAttributes.addFlashAttribute("errorMessage", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/settings";
        }
        redirectAttributes.addFlashAttribute("user", accountDto);
        accountService.updatePassword(username.get(), accountDto.getPassword());
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response) {
        Optional<String> username = accountService.getCurrentUsername();
        accountService.deleteAccount(username.get());
        new SecurityContextLogoutHandler().logout(request, response, null);
        redirectAttributes.addFlashAttribute("successMessage", "Account deleted succesfully!");
        return "redirect:/login";
    }
}
