package com.paymentchain.customer.controllers;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

import com.paymentchain.customer.bussiness.transaction.BussinessTransaction;
import com.paymentchain.customer.entities.CustomerEntity;
import com.paymentchain.customer.exception.BussinessRuleExcaption;
import com.paymentchain.customer.repositories.CustomerRepository;

@RestController
@RequestMapping("/customer/V1")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BussinessTransaction bussinessTransaction;

    @Autowired
    Environment env;


    @GetMapping("/check")
    public String getCheck() {
        return "Hello, your property value is " + env.getProperty("custom.activeprofileName");
    }

    @GetMapping()
    public ResponseEntity<List<CustomerEntity>> list() {
        List<CustomerEntity> list = customerRepository.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
    
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<CustomerEntity> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
           return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody CustomerEntity input) {
        Optional<CustomerEntity> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            CustomerEntity newCustomer = customer.get();
            newCustomer.setName(input.getName());
            newCustomer.setPhone(input.getPhone());
            newCustomer.setIban(input.getIban());
            newCustomer.setAddress(input.getAddress());
            newCustomer.setSurname(input.getSurname());
            newCustomer.setProducts(input.getProducts());
            newCustomer.setTransactions(input.getTransactions());
            CustomerEntity saved = customerRepository.save(newCustomer);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody CustomerEntity input) throws BussinessRuleExcaption, UnknownHostException {
        CustomerEntity saved = bussinessTransaction.saveCustomer(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/full")
    public CustomerEntity getCustomerByCode(@RequestParam("code") String code) {
        return bussinessTransaction.getCustomerByCode(code);
    }
}
