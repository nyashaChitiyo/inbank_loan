package com.inbank.finance.loanengine.calc;

import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;


public interface LoanAmountCalc {
    LoanResponse calMaxLoanAmount(LoanApplicationDto loanApplicationDto);
}
