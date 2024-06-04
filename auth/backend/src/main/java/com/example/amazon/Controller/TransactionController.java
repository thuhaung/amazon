package com.example.amazon.Controller;

import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Service.AuthUserTransactionService;
import com.example.amazon.Util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/transaction")
public class TransactionController {
    private final AuthUserTransactionService authUserTransactionService;

    /**
     * API endpoint that is polled by consumers to check transaction status
     * @param id - identifier of transaction to be checked
     * @return - status of transaction
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> getTransactionStatus(
        @PathVariable Long id
    ) {
        AuthUserTransaction transaction = authUserTransactionService.getTransactionById(id);
        String transactionStatus = transaction.getStatus().name();

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            transactionStatus
        );
    }
}
