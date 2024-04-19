package com.mfa272.dialogg.services;

import com.mfa272.dialogg.entities.Account;
import com.mfa272.dialogg.repositories.AccountRepository;
import com.mfa272.dialogg.dto.AccountRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountService {

    public enum RegistrationResult {
        SUCCESS,
        USERNAME_TAKEN,
        EMAIL_TAKEN
    }

    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResult registerUser(AccountRegistrationDTO userDTO) {
        Optional<Account> userByEmail = userRepository.findByEmail(userDTO.getEmail());
        Optional<Account> userByUsername = userRepository.findByUsername(userDTO.getUsername());
        
        if (userByEmail.isPresent()) {
            return RegistrationResult.EMAIL_TAKEN;
        }
        
        if (userByUsername.isPresent()) {
            return RegistrationResult.USERNAME_TAKEN;
        }
        
        Account user = new Account();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); 

        userRepository.save(user);

        return RegistrationResult.SUCCESS;
    }

    public boolean UserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
