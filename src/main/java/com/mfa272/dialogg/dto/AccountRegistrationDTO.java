package com.mfa272.dialogg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AccountRegistrationDTO {

    @Size(max = 16, message = "Maximum 16 characters")
    @Size(min = 6, message = "Minimum 6 characters")
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[\\S]*$", message = "Username must not contain whitespaces")
    private String username;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(max = 16, message = "Maximum 16 characters")
    @Size(min = 6, message = "Minimum 6 characters")
    @Pattern(regexp = "^[\\S]*$", message = "Password must not contain whitespaces")
    private String password;

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