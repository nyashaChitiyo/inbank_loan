package com.inbank.finance.loanengine.exception;

import com.inbank.finance.loanengine.exception.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(value = {InvalidLoanApplicationDetailsException.class})
    public ResponseEntity<Object> handleInvalidDetailsException(InvalidLoanApplicationDetailsException e){
        ExceptionDto exceptionDto = new ExceptionDto(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(exceptionDto,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {SegmentationException.class})
    public ResponseEntity<Object> handleSegmentationException(SegmentationException e){
        ExceptionDto exceptionDto = new ExceptionDto(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(exceptionDto,HttpStatus.BAD_REQUEST);
    }
}
