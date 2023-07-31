package com.inbank.finance.loanengine.exception;

public class InvalidLoanApplicationDetailsException extends RuntimeException{

    public InvalidLoanApplicationDetailsException(String message){
        super(message);
    }
}
