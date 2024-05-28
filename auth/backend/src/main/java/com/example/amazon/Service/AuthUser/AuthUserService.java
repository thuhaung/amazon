package com.example.amazon.Service.AuthUser;

import com.example.amazon.DTO.AuthUser.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthUserService extends UserDetailsService {
    AuthUserProjection getAuthUserById(Long userId);
    AuthUserPayload signUp(SignUpRequest request);
    void deactivateAuthUser(Long userId);
    void reactivateAuthUser(Long userId);
    void deleteAuthUser(Long userId);
    void deleteAuthUserImmediately(Long userId);
    void deleteAuthUserAndTransaction(Long userId);
    int removeInactiveUsers();
    void updatePassword(UpdatePasswordRequest request, Long userId);
    void updateEmail(UpdateEmailRequest request, Long userId);
}
