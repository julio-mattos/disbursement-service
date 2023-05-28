package com.sequraapi.disbursement.service.configuration;

import com.sequraapi.disbursement.service.exception.GeneralException;
import com.sequraapi.disbursement.service.exception.MerchantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MerchantNotFoundException.class})
    public ResponseEntity<GeneralException> handleMerchantException(MerchantNotFoundException ex){
        GeneralException generalException = new GeneralException(
                HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
        return new ResponseEntity<>(generalException, HttpStatus.valueOf(generalException.getCode()));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<GeneralException> handleException(Exception ex){
        GeneralException generalException = new GeneralException(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        return new ResponseEntity<>(generalException, HttpStatus.valueOf(generalException.getCode()));
    }
}
