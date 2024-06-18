package com.paymentchain.products.bussiness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.paymentchain.products.entities.ProductEntity;
import com.paymentchain.products.exception.BussinessRuleExcaption;
import com.paymentchain.products.repositories.ProductRepository;

@Service
public class BussinessTransaction {

    @Autowired
    private ProductRepository productRepository;

    public ProductEntity createProduct(ProductEntity input) throws BussinessRuleExcaption{
        if (input.getCode().isEmpty() || input.getName().isEmpty()) {
            throw new BussinessRuleExcaption("1070", "Code and Name product must be specified", HttpStatus.BAD_REQUEST);
        }

        ProductEntity newProduct = new ProductEntity();
        newProduct.setName(input.getName());
        newProduct.setCode(input.getCode());
        return productRepository.save(newProduct);
    }
}
