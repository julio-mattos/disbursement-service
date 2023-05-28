package com.sequraapi.disbursement.service.configuration;

import com.sequraapi.disbursement.service.exception.GeneralException;
import com.sequraapi.disbursement.service.exception.MerchantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<GeneralException> handleException(MerchantNotFoundException ex){
        GeneralException generalException = new GeneralException(
                HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity<>(generalException, HttpStatus.valueOf(generalException.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralException> handleException(Exception ex){
        GeneralException generalException = new GeneralException(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        return new ResponseEntity<>(generalException, HttpStatus.valueOf(generalException.getCode()));
    }
}
