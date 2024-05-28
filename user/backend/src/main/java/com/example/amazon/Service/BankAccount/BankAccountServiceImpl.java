package com.example.amazon.Service.BankAccount;

import com.example.amazon.DTO.BankAccount.BankAccountDTO;
import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Exception.Data.User.ExpiredBankAccountException;
import com.example.amazon.Model.BankAccount;
import com.example.amazon.Model.User;
import com.example.amazon.Repository.BankAccountRepository;
import com.example.amazon.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<BankAccountDTO> getBankAccountsByUserId(Long userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    @Override
    public BankAccount getBankAccountById(Long bankAccountId) {
        return bankAccountRepository.findById(bankAccountId).orElseThrow(
            () -> new ResourceNotFoundException("No bank account found with id " + bankAccountId + ".")
        );
    }

    private boolean isExpiryValid(Date date) {
        Instant expiry = date.toInstant();
        Instant now = Instant.now();

        return expiry.isAfter(now.plus(4L*7L, ChronoUnit.DAYS));
    }

    @Override
    @Transactional
    public BankAccountDTO addBankAccount(BankAccountDTO request, Long userId) {
        if (userRepository.existsById(userId)) {
            User user = userRepository.getReferenceById(userId);

            if (!isExpiryValid(request.getExpiry())) {
                throw new ExpiredBankAccountException("Bank account must not be expired or close to expiration.");
            }

            BankAccount bankAccount = modelMapper.map(request, BankAccount.class);
            bankAccount.setUser(user);

            if (request.isDefault()) {
                bankAccountRepository.removePreviousDefaultBankAccountSetting(userId);
            }

            bankAccountRepository.save(bankAccount);

            return request;
        }

        throw new ResourceNotFoundException("No user can be found with id " + userId + ".");
    }

    @Override
    @Transactional
    public int updateBankAccountById(BankAccountDTO request, Long bankAccountId, Long userId) {
        BankAccount bankAccount = getBankAccountById(bankAccountId);

        if (request.isDefault()) {
            bankAccountRepository.removePreviousDefaultBankAccountSetting(userId);
        }

        if (!isExpiryValid(request.getExpiry())) {
            bankAccount.setValid(false);
        }

        modelMapper.map(request, bankAccount);

        bankAccountRepository.save(bankAccount);

        return 1;
    }

    @Override
    public int deleteBankAccountById(Long bankAccountId) {
        bankAccountRepository.deleteById(bankAccountId);

        return 1;
    }
}
