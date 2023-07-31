package com.inbank.finance.loanengine.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class LoanApplicationDto {
    @NotNull
    private String personalCode;
    @NotNull
    private BigDecimal loanAmount;
    @Range(min = 1)
    private short loanPeriod;
}
