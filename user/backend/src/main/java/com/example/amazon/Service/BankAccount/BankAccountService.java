package com.example.amazon.Service.BankAccount;

import com.example.amazon.DTO.BankAccount.BankAccountDTO;
import com.example.amazon.Model.BankAccount;
import com.example.amazon.Controller.Payload.Request.BankAccount.BankAccountRequest;

import java.util.List;

public interface BankAccountService {
    List<BankAccountDTO> getBankAccountsByUserId(Long userId);
    BankAccount getBankAccountById(Long bankAccountId);
    BankAccountDTO addBankAccount(BankAccountDTO request, Long userId);
    int updateBankAccountById(BankAccountDTO request, Long bankAccountId, Long userId);
    int deleteBankAccountById(Long bankAccountId);
}
