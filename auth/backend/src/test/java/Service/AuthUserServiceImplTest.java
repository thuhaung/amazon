package Service;

import com.example.amazon.DTO.AuthUser.AuthUserPayload;
import com.example.amazon.DTO.AuthUser.SignUpRequest;
import com.example.amazon.DTO.AuthUser.UpdateEmailRequest;
import com.example.amazon.DTO.AuthUser.UpdatePasswordRequest;
import com.example.amazon.Exception.Data.EmailExistsException;
import com.example.amazon.Exception.Data.IncorrectPasswordException;
import com.example.amazon.Exception.Data.SamePasswordException;
import com.example.amazon.Kafka.Payload.UserEvent;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.AuthUserActionEnum;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Model.Role;
import com.example.amazon.Repository.AuthUserRepository;
import com.example.amazon.Repository.AuthUserTransactionRepository;
import com.example.amazon.Repository.RoleRepository;
import com.example.amazon.Service.AuthUser.AuthUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private AuthUserServiceImpl authUserService;

    @Test
    public void signUp_whenGivenUniqueEmail_shouldSignUpSuccessfully() {
        // given
        when(passwordEncoder.encode(anyString())).thenReturn("abc");

        Role role = Role.builder()
            .id(1)
            .type("User")
            .build();
        when(roleRepository.findByType("User")).thenReturn(Optional.of(role));

        SignUpRequest request = SignUpRequest.builder()
            .email("abc@gmail.com")
            .password("123456")
            .build();

        AuthUser user = AuthUser.builder()
            .id(1L)
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .isVerified(false)
            .isActive(true)
            .build();
        user.addRole(role);

        AuthUserTransaction transaction = AuthUserTransaction.builder()
            .id(1L)
            .type(AuthUserActionEnum.CREATE)
            .status(TransactionStatusEnum.PENDING)
            .user(user)
            .build();

        when(authUserRepository.save(any())).thenReturn(user);
        when(authUserTransactionRepository.save(any())).thenReturn(transaction);

        AuthUserPayload expected = AuthUserPayload.builder()
            .id(user.getId())
            .email(request.getEmail())
            .isVerified(user.isVerified())
            .isActive(user.isActive())
            .roles(List.of("User"))
            .build();

        when(modelMapper.map(any(), any())).thenReturn(expected);

        // when
        AuthUserPayload result = authUserService.signUp(request);

        // then
        verify(producer).sendUserCreatedEvent(any(UserEvent.class));
        assertEquals(expected, result);
    }

    @Test
    public void signUp_whenGivenDuplicateEmail_shouldThrowException() throws EmailExistsException {
        // given
        when(passwordEncoder.encode(anyString())).thenReturn("abc");

        Role role = Role.builder()
                .id(1)
                .type("User")
                .build();
        when(roleRepository.findByType("User")).thenReturn(Optional.of(role));

        SignUpRequest request = SignUpRequest.builder()
                .email("abc@gmail.com")
                .password("123456")
                .build();

        AuthUser user = AuthUser.builder()
                .id(1L)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isVerified(false)
                .isActive(true)
                .build();
        user.addRole(role);

        AuthUserTransaction transaction = AuthUserTransaction.builder()
                .id(1L)
                .type(AuthUserActionEnum.CREATE)
                .status(TransactionStatusEnum.PENDING)
                .user(user)
                .build();

        when(authUserRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        // then
        verify(producer, times(0)).sendUserCreatedEvent(any(UserEvent.class));
        assertThrows(EmailExistsException.class, () -> authUserService.signUp(request));
    }

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
        assertThrows(SamePasswordException.class, () -> authUserService.updatePassword(request, anyLong()));
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
        assertThrows(IncorrectPasswordException.class, () -> authUserService.updatePassword(request, anyLong()));
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
        authUserService.updatePassword(request, anyLong());

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
        assertThrows(EmailExistsException.class, () -> authUserService.updateEmail(request, 0L));
    }

    @Test
    public void updateEmail_whenGivenUniqueEmail_shouldUpdate() {
        // given
        UpdateEmailRequest request = UpdateEmailRequest.builder()
            .email("abc@gmail.com")
            .build();

        // when
        authUserService.updateEmail(request, 0L);

        // then
        verify(authUserRepository, times(1)).updateEmail(0L, request.getEmail());
    }
}
