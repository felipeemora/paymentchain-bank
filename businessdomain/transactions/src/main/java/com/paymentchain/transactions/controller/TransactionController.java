package com.paymentchain.transactions.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentchain.transactions.entities.TransactionEntity;
import com.paymentchain.transactions.repository.TransactionRepository;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping()
    public List<TransactionEntity> list() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<TransactionEntity> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
           return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody TransactionEntity input) {
        Optional<TransactionEntity> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            TransactionEntity newTransaction = transaction.get();
            newTransaction.setReference(input.getReference());
            newTransaction.setAccountIban(input.getAccountIban());
            newTransaction.setAmount(input.getAmount());
            newTransaction.setDate(input.getDate());
            newTransaction.setFee(input.getFee());
            newTransaction.setDescription(input.getDescription());
            newTransaction.setStatus(input.getStatus());
            newTransaction.setChannel(input.getChannel());
            TransactionEntity saved = transactionRepository.save(newTransaction);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/customer/transactions")
    public List<TransactionEntity> listByCustomer(@RequestParam("ibanAccount") String ibanAccount) {
        return transactionRepository.findByIbanAccount(ibanAccount);
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody TransactionEntity input) {
        TransactionEntity saved = transactionRepository.save(input);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        transactionRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
