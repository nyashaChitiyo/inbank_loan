package com.inbank.finance.loanengine.calc;

import com.inbank.finance.loanengine.dto.LoanApplicationDto;

import java.math.BigDecimal;

public interface CrediCalcStrategy {
    BigDecimal calcCreditScore(LoanApplicationDto loanApplicationDto);
}
