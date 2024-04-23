package com.mfa272.dialogg.services;

import com.mfa272.dialogg.entities.Account;
import com.mfa272.dialogg.repositories.AccountRepository;

import org.springframework.transaction.annotation.Transactional;

import com.mfa272.dialogg.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountService {

    public enum OperationResult {
        SUCCESS,
        USERNAME_TAKEN,
        EMAIL_TAKEN,
        FAILURE
    }

    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public OperationResult registerUser(AccountDTO userDTO) {
        Optional<Account> userByEmail = userRepository.findByEmail(userDTO.getEmail());
        Optional<Account> userByUsername = userRepository.findByUsername(userDTO.getUsername());

        if (userByEmail.isPresent()) {
            return OperationResult.EMAIL_TAKEN;
        }

        if (userByUsername.isPresent()) {
            return OperationResult.USERNAME_TAKEN;
        }

        Account user = new Account();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);

        return OperationResult.SUCCESS;
    }

    public boolean UserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional
    public boolean follow(String follower_username, String followed_username) {
        Optional<Account> follower = userRepository.findByUsername(follower_username);
        Optional<Account> followed = userRepository.findByUsername(followed_username);

        if (follower.isPresent() && followed.isPresent() && !follower_username.equals(followed_username)) {
            follower.get().addFollowing(followed.get());
            followed.get().addFollower(follower.get());
            return true;
        }

        return false;
    }

    @Transactional
    public boolean unfollow(String follower_username, String followed_username) {
        Optional<Account> follower = userRepository.findByUsername(follower_username);
        Optional<Account> followed = userRepository.findByUsername(followed_username);

        if (follower.isPresent() && followed.isPresent() && !follower_username.equals(followed_username)) {
            follower.get().removeFollowing(followed.get());
            followed.get().removeFollower(follower.get());
            return true;
        }

        return false;
    }

    public boolean isFollowing(String followerUsername, String followedUsername) {
        Optional<Account> follower = userRepository.findByUsername(followerUsername);
        Optional<Account> followed = userRepository.findByUsername(followedUsername);

        if (follower.isPresent() && followed.isPresent()) {
            return follower.get().getFollowing().contains(followed.get());
        }
        return false;
    }

    public Page<AccountDTO> getFollowers(String username, int page, int size) {
        Page<Account> followers = userRepository.findFollowersByUsername(username, PageRequest.of(page, size));
        return followers.map(f -> {
            AccountDTO dto = new AccountDTO();
            dto.setUsername(f.getUsername());
            return dto;
        });
    }

    public Page<AccountDTO> getFollowing(String username, int page, int size) {
        Page<Account> followers = userRepository.findFollowingByUsername(username, PageRequest.of(page, size));
        return followers.map(f -> {
            AccountDTO dto = new AccountDTO();
            dto.setUsername(f.getUsername());
            return dto;
        });
    }

    public long countFollowers(String username) {
        return userRepository.countFollowersByUsername(username);
    }

    public long countFollowing(String username) {
        return userRepository.countFollowingByUsername(username);
    }

    public OperationResult updateEmail(String username, String newEmail) {
        Optional<Account> account = userRepository.findByUsername(username);
        if(userRepository.findByEmail(newEmail).isPresent()){
            return OperationResult.EMAIL_TAKEN;
        }
        if (account.isPresent()) {
            Account a = account.get();
            a.setEmail(newEmail);
            userRepository.save(a);
            return OperationResult.SUCCESS;
        }
        return OperationResult.FAILURE;
    }

    public OperationResult updatePassword(String username, String newPassword) {
        Optional<Account> account = userRepository.findByUsername(username);
        if (account.isPresent()) {
            Account a = account.get();
            a.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(a);
            return OperationResult.SUCCESS;
        }
        return OperationResult.FAILURE;
    }

    @Transactional
    public OperationResult deleteAccount(String username) {
        int following = (int) countFollowing(username);
        if (following > 0) {
            for (AccountDTO u : getFollowing(username, 0, following)) {
                unfollow(username, u.getUsername());
            }
        }
        int followers = (int) countFollowing(username);
        if (followers > 0) {
            for (AccountDTO u : getFollowers(username, 0, followers)) {
                unfollow(u.getUsername(), username);
            }
        }
        userRepository.deleteByUsername(username);
        return OperationResult.SUCCESS;
    }

    public String getEmailByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username))
                .getEmail();
    }

    public Optional<String> getCurrentUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }
}
