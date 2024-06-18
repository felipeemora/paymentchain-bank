package com.paymentchain.customer.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BussinessRuleExcaption extends Exception {
    private long id;
    private String code;
    private HttpStatus httpStatus;

    public BussinessRuleExcaption(long id, String code, String message, HttpStatus httpStatus) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BussinessRuleExcaption(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BussinessRuleExcaption(String message, Throwable cause) {
        super(message, cause);
    }
}
