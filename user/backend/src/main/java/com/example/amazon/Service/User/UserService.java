package com.example.amazon.Service.User;

import com.example.amazon.DTO.User.UserDTO;
import com.example.amazon.Model.Enums.GenderEnum;
import com.example.amazon.Model.User;

import java.util.Date;

public interface UserService {
    User getUserById(Long id);
    UserDTO getUserDTOById(Long id);
    User addUserFromEvent(Long id);
    void removeUser(Long id);
    boolean existsById(Long id);
    int updateName(Long id, String name);
    int updateGender(Long id, GenderEnum gender);
    int updateBirthDate(Long id, Date birthDate);
    int updateMobile(Long id, String mobile);
}
