package com.mfa272.dialogg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AccountDTO {

    public interface Registration {
    }

    public interface UpdateEmail {
    }

    public interface UpdatePassword {
    }

    @Size(max = 16, message = "Maximum 16 characters", groups = {Registration.class})
    @Size(min = 6, message = "Minimum 6 characters", groups = {Registration.class})
    @NotBlank(message = "Username is required", groups = {Registration.class})
    @Pattern(regexp = "^[\\S]*$", message = "Username must not contain whitespaces", groups = {Registration.class})
    private String username;

    @Email(message = "Please provide a valid email", groups = {UpdateEmail.class})
    @NotBlank(message = "Email is required", groups = {UpdateEmail.class})
    private String email;

    @Size(max = 16, message = "Maximum 16 characters", groups = {UpdatePassword.class})
    @Size(min = 6, message = "Minimum 6 characters", groups = {UpdatePassword.class})
    @Pattern(regexp = "^[\\S]*$", message = "Password must not contain whitespaces", groups = {UpdatePassword.class})
    private String password;

    private String currentEmail;
    
    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}