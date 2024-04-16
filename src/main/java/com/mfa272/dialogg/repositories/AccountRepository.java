package com.mfa272.dialogg.repositories;

import com.mfa272.dialogg.entities.User;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}