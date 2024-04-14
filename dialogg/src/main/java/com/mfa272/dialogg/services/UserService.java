package com.mfa272.dialogg.services;

import com.mfa272.dialogg.entities.User;
import com.mfa272.dialogg.repositories.UserRepository;
import com.mfa272.dialogg.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    public enum RegistrationResult {
        SUCCESS,
        USERNAME_TAKEN,
        EMAIL_TAKEN
    }

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegistrationResult registerUser(UserRegistrationDTO userDTO) {
        Optional<User> userByEmail = userRepository.findByEmail(userDTO.getEmail());
        Optional<User> userByUsername = userRepository.findByUsername(userDTO.getUsername());
        
        if (userByEmail.isPresent()) {
            return RegistrationResult.EMAIL_TAKEN;
        }
        
        if (userByUsername.isPresent()) {
            return RegistrationResult.USERNAME_TAKEN;
        }
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); 

        userRepository.save(user);

        return RegistrationResult.SUCCESS;
    }
}
