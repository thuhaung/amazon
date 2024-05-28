package com.example.amazon.Repository;

import com.example.amazon.DTO.User.UserDTO;
import com.example.amazon.Model.Enums.GenderEnum;
import com.example.amazon.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT new com.example.amazon.DTO.User.UserDTO(x.name, x.birthDate, x.genderEnum, x.mobile) FROM User x WHERE x.id = ?1")
    Optional<UserDTO> findUserDTOById(Long id);
    @Query("SELECT x.nameChangedAt FROM User x WHERE x.id = ?1")
    Date getNameChangeDate(Long id);
    @Query("SELECT x.birthDateChangedAt FROM User x WHERE x.id = ?1")
    Date getBirthDateChangeDate(Long id);
    @Query("UPDATE User x SET x.name = ?2, x.nameChangedAt = CURRENT_TIMESTAMP WHERE x.id = ?1")
    @Modifying
    void updateName(Long id, String name);
    @Query("UPDATE User x SET x.birthDate = ?2, x.birthDateChangedAt = CURRENT_TIMESTAMP WHERE x.id = ?1")
    @Modifying
    void updateBirthDate(Long id, Date birthDate);
    @Query("UPDATE User x SET x.mobile = ?2 WHERE x.id = ?1")
    @Modifying
    void updateMobile(Long id, String mobile);
    @Query("UPDATE User x SET x.genderEnum = ?2 WHERE x.id = ?1")
    @Modifying
    void updateGender(Long id, GenderEnum gender);
}
