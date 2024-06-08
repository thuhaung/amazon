package com.example.amazon.Service;

import com.example.amazon.Exception.Data.UserCreationException;
import com.example.amazon.Exception.Kafka.KafkaException;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Repository.AuthUserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DistributedTransactionServiceTest {
    @Mock
    private Producer producer;
    @Mock
    private PollingService pollingService;
    @Mock
    private AuthUserService authUserService;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private AuthUserTransactionService authUserTransactionService;
    @InjectMocks
    private DistributedTransactionService distributedTransactionService;

    @Nested
    class DistributeSignUpEvent {
        @Captor
        private ArgumentCaptor<TransactionStatusEnum> enumCaptor;

        @Test
        public void givenSuccessfulConsumerProcessing_whenDistributeSignUpEvent_shouldUpdateTransactionToDone() {
            // given
            HttpStatusCode statusCode = HttpStatusCode.valueOf(200);

            when(pollingService.pollForUserEvent(anyLong(), anyLong())).thenReturn(statusCode);

            // when
            distributedTransactionService.distributeSignUpEvent(1L, 1L);

            // then
            verify(producer, times(1)).sendUserCreatedEvent(any());
            verify(authUserTransactionService, atLeastOnce()).updateTransactionStatus(anyLong(), enumCaptor.capture());
            assertEquals(enumCaptor.getValue(), TransactionStatusEnum.DONE);
        }

        @Test
        public void givenFailedConsumerProcessing_whenDistributeSignUpEvent_shouldUpdateTransactionToFailedAndThrowException() {
            // given
            HttpStatusCode statusCode = HttpStatusCode.valueOf(500);

            when(pollingService.pollForUserEvent(anyLong(), anyLong())).thenReturn(statusCode);

            // then
            assertThrows(UserCreationException.class, () -> distributedTransactionService.distributeSignUpEvent(1L, 1L));
            verify(authUserTransactionService, atLeastOnce()).updateTransactionStatus(anyLong(), enumCaptor.capture());
            verify(producer, times(1)).sendUserCreatedEvent(any());
            assertEquals(enumCaptor.getValue(), TransactionStatusEnum.FAILED);
        }

        @Test
        public void givenFailedProducerSending_whenDistributeSignUpEvent_shouldThrowException() {
            // given
            doThrow(KafkaException.class).when(producer).sendUserCreatedEvent(any());

            // then
            assertThrows(KafkaException.class, () -> distributedTransactionService.distributeSignUpEvent(1L, 1L));
        }
    }

    @Nested
    class DistributeEmailVerificationEvent {
        @Test
        public void givenUserId_whenDistributeEmailVerificationEvent_thenGenerateTokenAndSendEmail() {
            // given
            AuthUser user = AuthUser.builder().build();

            when(authUserService.getUserById(anyLong())).thenReturn(user);
            when(emailVerificationService.generateToken(any(AuthUser.class))).thenReturn("abc");

            // when
            distributedTransactionService.distributeEmailVerificationEvent(anyLong());

            // then
            verify(producer, times(1)).sendEmailVerificationToken(any());
        }
    }

    @Nested
    class HandleFailedCreationTransaction {
        @Test
        public void givenFailedUserCreationTransaction_whenHandleFailedCreationTransaction_thenDeleteUser() {
            // given
            AuthUserTransaction transaction = AuthUserTransaction.builder()
                .id(1L)
                .status(TransactionStatusEnum.FAILED)
                .build();

            when(authUserTransactionService.getTransactionById(anyLong())).thenReturn(transaction);

            // when
            distributedTransactionService.handleFailedCreationTransaction(1L, 1L);

            // then
            verify(authUserRepository, times(1)).deleteById(anyLong());
        }
    }
}
