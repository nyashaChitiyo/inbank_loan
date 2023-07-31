package com.inbank.finance.loanengine.service;

import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;

public interface LoanService {

    public LoanResponse loanDecision(LoanApplicationDto loanApplicationDto);
}
