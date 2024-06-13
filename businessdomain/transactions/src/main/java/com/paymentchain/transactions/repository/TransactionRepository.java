package com.paymentchain.transactions.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymentchain.transactions.entities.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT te FROM TransactionEntity te WHERE te.accountIban = ?1")
    public List<TransactionEntity> findByIbanAccount(String ibanAccount);
}
