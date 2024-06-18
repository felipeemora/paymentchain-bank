package com.paymentchain.products.controllers;

import com.paymentchain.products.entities.ProductEntity;
import com.paymentchain.products.repositories.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.paymentchain.products.bussiness.BussinessTransaction;
import com.paymentchain.products.exception.BussinessRuleExcaption;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BussinessTransaction bussinessTransaction;

    @GetMapping()
    public List<ProductEntity> list() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
           return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody ProductEntity input) {
        Optional<ProductEntity> customer = productRepository.findById(id);
        if (customer.isPresent()) {
            ProductEntity newProduct = customer.get();
            newProduct.setName(input.getName());
            newProduct.setCode(input.getCode());
            ProductEntity saved = productRepository.save(newProduct);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody ProductEntity input) throws BussinessRuleExcaption {
        ProductEntity saved = bussinessTransaction.createProduct(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        get(id);
        productRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
