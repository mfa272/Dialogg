package com.mfa272.dialogg.repositories;

import com.mfa272.dialogg.entities.Account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);

    void deleteByUsername(String username);

    @Query("SELECT f FROM Account a JOIN a.followers f WHERE a.username = :username")
    Page<Account> findFollowersByUsername(@Param("username") String username, Pageable page);

    @Query("SELECT f FROM Account a JOIN a.following f WHERE a.username = :username")
    Page<Account> findFollowingByUsername(@Param("username") String username, Pageable page);

    @Query("SELECT COUNT(*) FROM Account a JOIN a.followers f WHERE a.username = :username")    
    Long countFollowersByUsername(@Param("username") String username);

    @Query("SELECT COUNT(*) FROM Account a JOIN a.following f WHERE a.username = :username")    
    Long countFollowingByUsername(@Param("username") String username);

    @Query("SELECT COUNT(a) > 0 FROM Account a JOIN a.following f WHERE a.username = :username AND f.username = :username2")
    boolean isUserFollowingUser2(@Param("username") String username, @Param("username2") String username2);
}
