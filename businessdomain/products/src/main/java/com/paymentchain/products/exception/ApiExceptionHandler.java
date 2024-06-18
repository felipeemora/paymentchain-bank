package com.paymentchain.products.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.paymentchain.products.common.StandarizedApiExceptionResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BussinessRuleExcaption.class)
    public ResponseEntity<?> handleBussinessRuleException(BussinessRuleExcaption e) {
        StandarizedApiExceptionResponse standarizedApiExceptionResponse = new StandarizedApiExceptionResponse(
            "TECNICO", "Error Valiadtion", e.getCode(), e.getMessage(), null
            );

        return ResponseEntity.status(e.getHttpStatus()).body(standarizedApiExceptionResponse);
    }
}
