package com.example.amazon.Repository;

import com.example.amazon.Model.UserTransaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserTransactionRepository extends CrudRepository<UserTransaction, Long> {
    @Query("SELECT x FROM UserTransaction x WHERE x.status in ('PENDING', 'PROCESSING')")
    List<UserTransaction> getUnfinishedTransactions();
    @Query("DELETE FROM UserTransaction x WHERE x.referencedAuthId=?1")
    @Modifying
    void removeTransactionByReferenceId(Long id);
    @Query("SELECT COUNT(x)>0 FROM UserTransaction x WHERE x.referencedAuthId=?1")
    boolean existsByReferenceId(Long id);
    @Query("UPDATE UserTransaction x SET x.status='DONE' WHERE x.id=?1")
    @Modifying
    void finishTransaction(Long id);
}
