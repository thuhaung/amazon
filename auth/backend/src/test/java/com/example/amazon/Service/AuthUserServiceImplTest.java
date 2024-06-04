package com.example.amazon.Service;

import com.example.amazon.DTO.AuthUser.UpdateEmailRequest;
import com.example.amazon.DTO.AuthUser.UpdatePasswordRequest;
import com.example.amazon.Exception.Data.EmailExistsException;
import com.example.amazon.Exception.Data.IncorrectPasswordException;
import com.example.amazon.Exception.Data.SamePasswordException;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Repository.AuthUserRepository;
import com.example.amazon.Repository.AuthUserTransactionRepository;
import com.example.amazon.Repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceImplTest {

    @Mock
    private AuthUserTransactionRepository authUserTransactionRepository;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Producer producer;
    @InjectMocks
    private AuthUserService authUserService;

    @Test
    public void updatePassword_whenGivenOldPassword_shouldThrowException() throws SamePasswordException {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("a")
            .newPassword("a")
            .build();

        when(authUserRepository.getPasswordById(anyLong())).thenReturn("a");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // then
        assertThrows(SamePasswordException.class, () -> authUserService.updatePassword(anyLong(), request));
    }

    @Test
    public void updatePassword_whenGivenIncorrectPassword_shouldThrowException() throws IncorrectPasswordException {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .oldPassword("a")
                .newPassword("b")
                .build();

        when(authUserRepository.getPasswordById(anyLong())).thenReturn("c");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // then
        assertThrows(IncorrectPasswordException.class, () -> authUserService.updatePassword(anyLong(), request));
    }

    @Test
    public void updatePassword_whenGivenPassword_shouldUpdate() {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .oldPassword("a")
                .newPassword("b")
                .build();

        when(authUserRepository.getPasswordById(anyLong())).thenReturn("a");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("");

        // when
        authUserService.updatePassword(anyLong(), request);

        // then
        verify(authUserRepository, times(1)).updatePassword(anyLong(), anyString());
    }

    @Test
    public void updateEmail_whenGivenDuplicateEmail_shouldThrowException() throws EmailExistsException {
        // given
        UpdateEmailRequest request = UpdateEmailRequest.builder()
            .email("abc@gmail.com")
            .build();

        doThrow(EmailExistsException.class).when(authUserRepository).updateEmail(anyLong(), anyString());

        // then
        assertThrows(EmailExistsException.class, () -> authUserService.updateEmail(0L, request));
    }

    @Test
    public void updateEmail_whenGivenUniqueEmail_shouldUpdate() {
        // given
        UpdateEmailRequest request = UpdateEmailRequest.builder()
            .email("abc@gmail.com")
            .build();

        // when
        authUserService.updateEmail(0L, request);

        // then
        verify(authUserRepository, times(1)).updateEmail(0L, request.getEmail());
    }
}
