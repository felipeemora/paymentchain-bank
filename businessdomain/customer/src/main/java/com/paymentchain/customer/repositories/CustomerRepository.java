package com.paymentchain.customer.repositories;

import com.paymentchain.customer.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    @Query("SELECT c FROM CustomerEntity c WHERE c.code = ?1")
    public CustomerEntity findByCode(String code);

    @Query("SELECT c FROM CustomerEntity c WHERE c.iban = ?1")
    public CustomerEntity findByIban(String code);
}
