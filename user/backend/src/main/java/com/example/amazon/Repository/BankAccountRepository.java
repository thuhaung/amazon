package com.example.amazon.Repository;

import com.example.amazon.DTO.BankAccount.BankAccountDTO;
import com.example.amazon.Model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    @Query("SELECT new com.example.amazon.DTO.BankAccount.BankAccountDTO(x.accountNumber, x.bankName, x.expiry, x.isDefault, x.isValid) FROM BankAccount x WHERE x.user.id =?1")
    List<BankAccountDTO> findByUserId(Long id);
    @Query("UPDATE BankAccount x SET x.isDefault = false WHERE x.isDefault = true AND x.user.id = ?1")
    @Modifying
    void removePreviousDefaultBankAccountSetting(Long userId);
}
