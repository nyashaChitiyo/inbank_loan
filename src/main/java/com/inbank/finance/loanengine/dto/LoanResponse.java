package com.inbank.finance.loanengine.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanResponse {
    private boolean result;
    private BigDecimal maxApprovedAmount;
    private BigDecimal approvedPeriod;
}
