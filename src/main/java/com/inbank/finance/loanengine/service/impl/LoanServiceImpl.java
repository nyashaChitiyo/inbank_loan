package com.inbank.finance.loanengine.service.impl;

import com.inbank.finance.loanengine.calc.CrediCalcStrategy;
import com.inbank.finance.loanengine.calc.LoanAmountCalc;
import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;
import com.inbank.finance.loanengine.exception.InvalidLoanApplicationDetailsException;
import com.inbank.finance.loanengine.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoanServiceImpl implements LoanService {

    private final CrediCalcStrategy crediCalcStrategy;
    private final LoanAmountCalc loanAmountCalc;
    private static final Logger LOG = LoggerFactory.getLogger(LoanServiceImpl.class);

    public LoanServiceImpl(CrediCalcStrategy crediCalcStrategy, LoanAmountCalc loanAmountCalc) {
        this.crediCalcStrategy = crediCalcStrategy;
        this.loanAmountCalc = loanAmountCalc;
    }

    @Override
    public LoanResponse loanDecision(LoanApplicationDto loanApplicationDto) {
        BigDecimal amount = loanApplicationDto.getLoanAmount();
        BigDecimal min = new BigDecimal(2000); //store in database
        BigDecimal max = new BigDecimal(10000); //

        // validate loan amount, should be in the range between 2000-10000
        if(min.compareTo(amount) == 1 || amount.compareTo(max) == 1){
            throw new InvalidLoanApplicationDetailsException("Amount can only be greater than 2000 and less than 10000");
        }

        return loanEligibility(loanApplicationDto);
    }
    public LoanResponse loanEligibility(LoanApplicationDto loanApplicationDto){
        validateLoanDto(loanApplicationDto);

        //Calculate credit score
        BigDecimal creditScore  = crediCalcStrategy.calcCreditScore(loanApplicationDto);
        //If credit score is less than 1 find calculate minimun approved amount else approve and ind the most amount that can be granted
        if(creditScore == null || creditScore.compareTo(BigDecimal.valueOf(1)) == -1){
            var approvedAmount = loanAmountCalc.calMaxLoanAmount(loanApplicationDto);
            LOG.info("Amount allowed {}", approvedAmount);
            return approvedAmount;
        }
        else {
            var approvedAmount = loanAmountCalc.calMaxLoanAmount(loanApplicationDto);
            LOG.info("Amount allowed {}", approvedAmount);
            return approvedAmount;
        }
    }

    private void validateLoanDto(LoanApplicationDto loanApplicationDto){
        if(loanApplicationDto == null || loanApplicationDto.getLoanAmount() == null ||
                loanApplicationDto.getPersonalCode() == null || loanApplicationDto.getPersonalCode().trim().isEmpty()){
            throw new InvalidLoanApplicationDetailsException("Please provide all loan parameters");
        }
        if(loanApplicationDto.getLoanPeriod() <= 0 ){
            throw new InvalidLoanApplicationDetailsException("Loan period cannot be 0 or less than zero");
        }
    }
}
