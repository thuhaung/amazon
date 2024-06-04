package com.example.amazon.Repository;

import com.example.amazon.DTO.EmailVerificationToken.EmailVerificationTokenProjection;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByUserId(Long id);
    @Query
    @Modifying
    void deleteByUserId(Long id);
}
