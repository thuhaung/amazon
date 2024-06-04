package com.example.amazon.Repository;

import com.example.amazon.Model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("SELECT x.expiration FROM RefreshToken x WHERE x.user.id=?1")
    Instant findExpirationByUserId(Long id);
    @Modifying
    int deleteByUserId(Long id);
}
