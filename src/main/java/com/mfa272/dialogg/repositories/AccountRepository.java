package com.mfa272.dialogg.repositories;

import com.mfa272.dialogg.entities.Account;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);
}