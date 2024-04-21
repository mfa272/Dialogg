package com.mfa272.dialogg.services;

import com.mfa272.dialogg.entities.Account;
import com.mfa272.dialogg.repositories.AccountRepository;

import org.springframework.transaction.annotation.Transactional;

import com.mfa272.dialogg.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

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

    public RegistrationResult registerUser(AccountDTO userDTO) {
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

    @Transactional
    public boolean follow(String follower_username, String followed_username) {
        Account follower = userRepository.findByUsername(follower_username).orElse(null);
        Account followed = userRepository.findByUsername(followed_username).orElse(null);

        if (follower != null && followed != null && follower_username != followed_username) {
            follower.addFollowing(followed);
            followed.addFollower(follower);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean unfollow(String follower_username, String followed_username) {
        Account follower = userRepository.findByUsername(follower_username).orElse(null);
        Account followed = userRepository.findByUsername(followed_username).orElse(null);

        if (follower != null && followed != null && follower_username != followed_username) {
            follower.removeFollowing(followed);
            followed.removeFollower(follower);
            return true;
        }

        return false;
    }

    public boolean isFollowing(String followerUsername, String followedUsername) {
        Account follower = userRepository.findByUsername(followerUsername).orElse(null);
        Account followed = userRepository.findByUsername(followedUsername).orElse(null);

        if (follower != null && followed != null) {
            return follower.getFollowing().contains(followed);
        }
        return false;
    }

    public Page<AccountDTO> getFollowers(String username, int page, int size){
        Page<Account> followers = userRepository.findFollowersByUsername(username, PageRequest.of(page, size));
        return followers.map(f -> {
            AccountDTO dto = new AccountDTO();
            dto.setUsername(f.getUsername());
            return dto;
        });
    }

    public Page<AccountDTO> getFollowing(String username, int page, int size){
        Page<Account> followers = userRepository.findFollowingByUsername(username, PageRequest.of(page, size));
        return followers.map(f -> {
            AccountDTO dto = new AccountDTO();
            dto.setUsername(f.getUsername());
            return dto;
        });
    }

    public long countFollowers(String username){
        return userRepository.countFollowersByUsername(username);
    }

    public long countFollowing(String username){
        return userRepository.countFollowingByUsername(username);
    }
}
