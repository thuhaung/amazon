package com.example.amazon.Service.AuthUser;

import com.example.amazon.DTO.AuthUser.*;
import com.example.amazon.Exception.Data.*;
import com.example.amazon.Kafka.Payload.UserEvent;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.AuthUserActionEnum;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Repository.AuthUserRepository;
import com.example.amazon.Repository.AuthUserTransactionRepository;
import com.example.amazon.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.KafkaException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserTransactionRepository authUserTransactionRepository;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Producer producer;

    @Override
    public AuthUserProjection getAuthUserById(Long userId) {
        return authUserRepository.findProjectionById(userId).orElseThrow(
            () -> new ResourceNotFoundException("No user can be found with id " + userId + ".")
        );
    }

    @Override
    @Transactional
    public AuthUserPayload signUp(SignUpRequest request) {
        AuthUser user = AuthUser.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .isVerified(false)
                                .isActive(true)
                                .build();

        user.addRole(roleRepository.findByType("User").get());

        try {
            // persist new user
            user = authUserRepository.save(user);
            log.info("New user created.");

            // create transaction
            AuthUserTransaction transaction = AuthUserTransaction.builder()
                    .type(AuthUserActionEnum.CREATE)
                    .status(TransactionStatusEnum.PENDING)
                    .user(user)
                    .build();
            authUserTransactionRepository.save(transaction);
            log.info("Transaction created.");

            producer.sendUserCreatedEvent(new UserEvent(
                user.getId(),
                transaction.getId()
            ));
        } catch (DataIntegrityViolationException e) {
            log.error("Email already exists.");
            throw new EmailExistsException("Email already exists.");
        } catch (KafkaException e) {
            log.error("Unable to send creation event to user auth topic due to " + e.getMessage());
            throw new KafkaException("Unable to process registration. Please try again later.");
        }

        return modelMapper.map(user, AuthUserPayload.class);
    }

    @Override
    @Transactional
    public void deactivateAuthUser(Long userId) {
        authUserRepository.deactivateUser(userId, null);
    }

    @Override
    @Transactional
    public void reactivateAuthUser(Long userId) {
        authUserRepository.reactivateUser(userId);
    }

    @Override
    @Transactional
    public void deleteAuthUser(Long userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);

        authUserRepository.deactivateUser(userId, calendar.getTime());
    }

    @Override
    @Transactional
    public void deleteAuthUserImmediately(Long userId) {
        authUserRepository.deleteById(userId);
        log.info("Removed user id " + userId + ".");
    }

    @Override
    @Transactional
    public void deleteAuthUserAndTransaction(Long userId) {
        authUserRepository.deleteById(userId);
        authUserTransactionRepository.deleteTransactionsByUserId(userId);
        log.info("Removed user id " + userId + " and associated transactions.");
    }

    @Override
    @Transactional
    public int removeInactiveUsers() {
        return authUserRepository.removeInactiveUsers();
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request, Long userId) {
        String password = authUserRepository.getPasswordById(userId);
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();

        if (passwordEncoder.matches(oldPassword, password)) {
            // verifies if new password matches old password
            if (passwordEncoder.matches(newPassword, password)) {
                throw new SamePasswordException("New password cannot be the same as current password.");
            }
            else {
                authUserRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
            }
        }
        else {
            // old password given is incorrect
            throw new IncorrectPasswordException("Current password is incorrect.");
        }
    }

    @Override
    @Transactional
    public void updateEmail(UpdateEmailRequest request, Long userId) {
        String email = request.getEmail();

        try {
            authUserRepository.updateEmail(userId, email);
        } catch (DataIntegrityViolationException e) {
            log.error("Email already exists.");

            throw new EmailExistsException("Email already exists.");
        }
    }

    public void verify(Long userId) {
        authUserRepository.verifyUser(userId);
    }

    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String username) {
        return authUserRepository.findByEmail(username).orElseThrow(
            () -> new ResourceNotFoundException("No user can be found with email " + username + ".")
        );
    }
}
