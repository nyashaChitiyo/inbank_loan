package com.inbank.finance.loanengine.calc.impl;

import com.inbank.finance.loanengine.calc.CrediCalcStrategy;
import com.inbank.finance.loanengine.dto.LoanApplicationDto;
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
public class CreditStrategy implements CrediCalcStrategy {

    private final UserRepository userRepository;
    private final SegmentationRepository segmentationRepository;
    private static final Logger LOG = LoggerFactory.getLogger(CreditStrategy.class);

    public CreditStrategy(UserRepository userRepository, SegmentationRepository segmentationRepository) {
        this.userRepository = userRepository;
        this.segmentationRepository = segmentationRepository;
    }

    public BigDecimal calcCreditScore(LoanApplicationDto loanApplicationDto){

        validateLoanDto(loanApplicationDto);
        String personalCode = loanApplicationDto.getPersonalCode();
        BigDecimal loanAmount = loanApplicationDto.getLoanAmount();
        short loanPeriod = loanApplicationDto.getLoanPeriod();

        User user = userRepository.findById(personalCode).orElseThrow(() -> new UserException("User with personal code "+personalCode+" does not exist"));
        Segmentation segmentation = segmentationRepository.findById(user.getSegmentId())
                .orElseThrow(() -> {throw new SegmentationException("Segmentation ID not found");});
        LOG.info("Segmentation {}",segmentation);
        //Decline loan if user is in debt else calculate credit score
        if(segmentation.getSegmentId().equals("49002010965")){
            throw new SegmentationException("User is in DEBT");
        }
        BigDecimal creditScore = null;
            creditScore = (BigDecimal.valueOf(segmentation.getCreditModifier()).divide(loanAmount,new MathContext(3))).multiply(BigDecimal.valueOf(loanPeriod)).stripTrailingZeros();
        LOG.info("CREDIT SCORE {}",creditScore);
        return creditScore;
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
