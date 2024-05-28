package com.example.amazon.Service.User;

import com.example.amazon.DTO.User.UserDTO;
import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Exception.Data.User.UserAlreadyExistsException;
import com.example.amazon.Exception.Data.User.UserInfoException;
import com.example.amazon.Model.Enums.GenderEnum;
import com.example.amazon.Model.User;
import com.example.amazon.Repository.UserRepository;
import com.example.amazon.Util.DateConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("No user can be found with id " + id + ".")
        );
    }

    @Override
    public UserDTO getUserDTOById(Long id) {
        return userRepository.findUserDTOById(id).orElseThrow(
            () -> new ResourceNotFoundException("No user can be found with id " + id + ".")
        );
    }

    @Override
    public boolean existsById(Long id) {
        Boolean exists = userRepository.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("No user can be found with id " + id + ".");
        }

        return exists;
    }



    @Override
    @Transactional
    public User addUserFromEvent(Long id) {
        if (userRepository.existsById(id)) {
            log.error("User with id " + id + " already exists.");
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
            .id(id)
            .build();

        userRepository.save(user);
        log.info("User with id " + id + " added successfully.");

        return user;
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    private boolean isUpdateEligible(Date date) {
        LocalDate from = DateConverter.convertToLocalDate(date);
        LocalDate to = LocalDate.now();

        return ChronoUnit.DAYS.between(from, to) >= 7;
    }

    private boolean isOldEnough(Date date) {
        LocalDate from = DateConverter.convertToLocalDate(date);
        LocalDate to = LocalDate.now();

        return ChronoUnit.YEARS.between(from, to) >= 13;
    }

    @Override
    @Transactional
    public int updateName(Long id, String name) {
        Date lastUpdate = userRepository.getNameChangeDate(id);

        if ((lastUpdate == null) || (isUpdateEligible(lastUpdate))) {
            userRepository.updateName(id, name);

            return 1;
        }

        throw new UserInfoException("Name has already been changed within the past 7 days.");
    }

    @Override
    @Transactional
    public int updateGender(Long id, GenderEnum gender) {
        userRepository.updateGender(id, gender);

        return 1;
    }

    @Override
    @Transactional
    public int updateBirthDate(Long id, Date birthDate) {
        Date lastUpdate = userRepository.getBirthDateChangeDate(id);

        if ((lastUpdate == null) || (isUpdateEligible(lastUpdate))) {
            if (!isOldEnough(birthDate)){
                throw new UserInfoException("Must be above 13 years old.");
            }

            userRepository.updateBirthDate(id, birthDate);

            return 1;
        }

        throw new UserInfoException("Birthdate has already been changed within the past 7 days.");
    }

    @Override
    @Transactional
    public int updateMobile(Long id, String mobile) {
        userRepository.updateMobile(id, mobile);

        return 1;
    }
}
