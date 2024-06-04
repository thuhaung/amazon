package com.example.amazon.Repository;

import com.example.amazon.Model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    @Query("UPDATE AuthUser x SET x.isActive=?2, x.deletionDate=?3 WHERE x.id=?1")
    @Modifying
    void updateActiveStatusById(Long id, boolean isActive, Date deletionDate);
    @Query("UPDATE AuthUser x SET x.isVerified=?2 WHERE x.id=?1")
    @Modifying
    void updateVerificationStatusById(Long id, boolean isVerified);
    @Query("SELECT x FROM AuthUser x INNER JOIN FETCH x.roles y WHERE x.email=?1")
    Optional<AuthUser> findByEmail(String email);
    @Query("SELECT x.email FROM AuthUser x WHERE x.id=?1")
    String getEmailById(Long id);
    @Query("SELECT x.password FROM AuthUser x WHERE x.id=?1")
    String getPasswordById(Long id);
    @Query("UPDATE AuthUser x SET x.password=?2 WHERE x.id=?1")
    @Modifying
    void updatePassword(Long id, String encodedPassword);
    @Query("UPDATE AuthUser x SET x.email=?2, x.isVerified=false WHERE x.id=?1")
    @Modifying
    void updateEmail(Long id, String email);
    boolean existsByEmail(String email);
    @Query("DELETE FROM AuthUser x WHERE x.deletionDate < CURRENT_TIMESTAMP AND x.isActive=false")
    @Modifying
    int removeInactiveUsers();
}
