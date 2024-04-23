package com.mfa272.dialogg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AccountDTO {

    public interface RegistrationForm {
    }

    public interface UpdateEmailForm {
    }

    public interface UpdatePasswordForm {
    }

    @Size(max = 16, message = "Maximum 16 characters", groups = { RegistrationForm.class })
    @Size(min = 6, message = "Minimum 6 characters", groups = { RegistrationForm.class })
    @NotBlank(message = "Username is required", groups = { RegistrationForm.class })
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username contains unallowed characters", groups = {
            RegistrationForm.class })
    private String username;

    @Email(message = "Please provide a valid email", groups = { UpdateEmailForm.class, RegistrationForm.class })
    @NotBlank(message = "Email is required", groups = { UpdateEmailForm.class, RegistrationForm.class })
    private String email;

    @Size(max = 16, message = "Maximum 16 characters", groups = { UpdatePasswordForm.class, RegistrationForm.class })
    @Size(min = 6, message = "Minimum 6 characters", groups = { UpdatePasswordForm.class, RegistrationForm.class })
    @Pattern(regexp = "^[\\S]*$", message = "Password must not contain whitespaces", groups = {
            UpdatePasswordForm.class, RegistrationForm.class })
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
