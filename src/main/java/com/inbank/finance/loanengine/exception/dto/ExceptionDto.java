package com.inbank.finance.loanengine.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class ExceptionDto {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime time;
}
