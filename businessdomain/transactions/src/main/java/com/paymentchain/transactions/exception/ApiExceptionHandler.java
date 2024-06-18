package com.paymentchain.transactions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.paymentchain.transactions.common.StandarizedApiExceptionResponse;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknowHostException(Exception e) {
        StandarizedApiExceptionResponse standarizedApiExceptionResponse = new StandarizedApiExceptionResponse(
            "TECNICO", "Error en la entrada y salida de datos", "1024", e.getMessage(), null
            );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(standarizedApiExceptionResponse);
    }

    @ExceptionHandler(BussinessRuleExcaption.class)
    public ResponseEntity<?> handleBussinessRuleException(BussinessRuleExcaption e) {
        StandarizedApiExceptionResponse standarizedApiExceptionResponse = new StandarizedApiExceptionResponse(
            "TECNICO", "Error Valiadtion", e.getCode(), e.getMessage(), null
            );

        return ResponseEntity.status(e.getHttpStatus()).body(standarizedApiExceptionResponse);
    }
}
