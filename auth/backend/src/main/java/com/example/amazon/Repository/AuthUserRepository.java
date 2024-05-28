package com.example.amazon.Repository;

import com.example.amazon.DTO.AuthUser.AuthUserProjection;
import com.example.amazon.Model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    @Query("SELECT x FROM AuthUser x INNER JOIN FETCH x.roles y WHERE x.email=?1")
    Optional<AuthUser> findByEmail(String email);
    @Query("SELECT x.id as id, x.email as email, x.isVerified as isVerified, x.isActive as isActive, y.type as roles FROM AuthUser x LEFT JOIN x.roles y WHERE x.id=?1")
    Optional<AuthUserProjection> findProjectionById(Long id);
    @Query("SELECT x.password FROM AuthUser x WHERE x.id=?1")
    String getPasswordById(Long id);
    @Query("UPDATE AuthUser x SET x.isActive=false, x.deletionDate=?2 WHERE x.id=?1")
    @Modifying
    void deactivateUser(Long id, Date deletionDate);
    @Query("UPDATE AuthUser x SET x.isActive=true, x.deletionDate=NULL WHERE x.id=?1")
    @Modifying
    void reactivateUser(Long id);
    @Query("UPDATE AuthUser x SET x.password=?2 WHERE x.id=?1")
    @Modifying
    void updatePassword(Long id, String encodedPassword);
    @Query("UPDATE AuthUser x SET x.email=?2, x.isVerified=false WHERE x.id=?1")
    @Modifying
    void updateEmail(Long id, String email);
    @Query("UPDATE AuthUser x SET x.isVerified=true WHERE x.id=?1")
    @Modifying
    void verifyUser(Long id);
    @Query("DELETE FROM AuthUser x WHERE x.deletionDate < CURRENT_TIMESTAMP AND x.isActive=false")
    @Modifying
    int removeInactiveUsers();
}
