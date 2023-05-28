package com.sequraapi.disbursement.service.exception;

public class MerchantNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public MerchantNotFoundException(){
        super("Merchant not found!");
    }

    public MerchantNotFoundException(String message){
        super(message);
    }

}
