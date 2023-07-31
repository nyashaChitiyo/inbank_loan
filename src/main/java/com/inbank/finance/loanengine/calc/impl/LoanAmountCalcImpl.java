package com.inbank.finance.loanengine.calc.impl;

import com.inbank.finance.loanengine.calc.CrediCalcStrategy;
import com.inbank.finance.loanengine.calc.LoanAmountCalc;
import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;
import com.inbank.finance.loanengine.entity.Segmentation;
import com.inbank.finance.loanengine.entity.User;
import com.inbank.finance.loanengine.exception.SegmentationException;
import com.inbank.finance.loanengine.exception.UserException;
import com.inbank.finance.loanengine.repository.SegmentationRepository;
import com.inbank.finance.loanengine.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class LoanAmountCalcImpl implements LoanAmountCalc {

    private final CrediCalcStrategy crediCalcStrategy;
    private final UserRepository userRepository;
    private final SegmentationRepository segmentationRepository;
    private static final Logger LOG = LoggerFactory.getLogger(LoanAmountCalcImpl.class);
    public LoanAmountCalcImpl(CrediCalcStrategy crediCalcStrategy, UserRepository userRepository, SegmentationRepository segmentationRepository) {
        this.crediCalcStrategy = crediCalcStrategy;
        this.userRepository = userRepository;
        this.segmentationRepository = segmentationRepository;
    }

    @Override
    public LoanResponse calMaxLoanAmount(LoanApplicationDto loanApplicationDto) {

        validateLoanDto(loanApplicationDto);
        String personalCode = loanApplicationDto.getPersonalCode();

        User user = userRepository.findById(personalCode).orElseThrow(() -> new UserException("User with personal code "+personalCode+" does not exist"));
        BigDecimal loanPeriod = BigDecimal.valueOf(loanApplicationDto.getLoanPeriod());

        Segmentation segmentation = segmentationRepository.findById(user.getSegmentId())
                .orElseThrow(() -> {throw new SegmentationException("Segmentation ID not found");});
        LOG.info("Segmentation {}",segmentation);

        BigDecimal creditModifier = BigDecimal.valueOf(segmentation.getCreditModifier());
        BigDecimal amount = null;
        BigDecimal maxPeriod = loanPeriod;
        amount = calcLoanAmount(loanPeriod,creditModifier);

        // change loan period to accomodate amount approved
        if(amount.compareTo(BigDecimal.valueOf(2000)) == -1){
            maxPeriod = calcPeriod(creditModifier,BigDecimal.valueOf(2000));
            amount = BigDecimal.valueOf(2000);
            LOG.info("modied period is {} and amount is {}",maxPeriod,BigDecimal.valueOf(2000));
        }
        else if (BigDecimal.valueOf(10000).compareTo(amount) == -1){
            LOG.info("amount is greater than 10000 {}",amount);
            maxPeriod = calcPeriod(creditModifier,BigDecimal.valueOf(10000));
            amount = BigDecimal.valueOf(10000);
            LOG.info("modied period is {} and amount is {}",maxPeriod,amount);
        }

        LoanResponse response = new LoanResponse();
        response.setApprovedPeriod(maxPeriod);
        response.setMaxApprovedAmount(amount);
        response.setResult(true);
        return response;
    }
    private BigDecimal calcPeriod(BigDecimal creditModifier, BigDecimal amount){
        if(amount == null || creditModifier == null)
            throw new IllegalArgumentException("amount or creditModifier cannot be null");
        BigDecimal period = new BigDecimal((amount.divide(creditModifier,new MathContext(3))).multiply(BigDecimal.valueOf(1)).stripTrailingZeros().toPlainString());
        return period;
    }

    private BigDecimal calcLoanAmount(BigDecimal loanPeriod, BigDecimal creditModifier){
        if(loanPeriod == null || creditModifier == null)
            throw new IllegalArgumentException("loanPeriod or creditModifier cannot be null");
        LOG.info("Loan Period {} Credit Modifier {}", loanPeriod, creditModifier);
        return  new BigDecimal((loanPeriod.divide(BigDecimal.valueOf(1),new MathContext(3))).multiply(creditModifier).stripTrailingZeros().toPlainString());
    }
    private void validateLoanDto(LoanApplicationDto loanApplicationDto){
        if(loanApplicationDto == null || loanApplicationDto.getLoanAmount() == null ||
                loanApplicationDto.getPersonalCode() == null || loanApplicationDto.getPersonalCode().trim().isEmpty()){
            throw new IllegalArgumentException("Please provide all loan parameters");
        }
        if(loanApplicationDto.getLoanPeriod() <= 0 ){
            throw new IllegalArgumentException("Loan period cannot be 0 or less than zero");
        }
    }
}
