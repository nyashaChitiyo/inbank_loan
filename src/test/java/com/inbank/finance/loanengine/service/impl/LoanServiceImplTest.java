package com.inbank.finance.loanengine.service.impl;

import com.inbank.finance.loanengine.calc.CrediCalcStrategy;
import com.inbank.finance.loanengine.calc.LoanAmountCalc;
import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;
import com.inbank.finance.loanengine.exception.InvalidLoanApplicationDetailsException;
import com.inbank.finance.loanengine.repository.SegmentationRepository;
import com.inbank.finance.loanengine.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @InjectMocks
    LoanServiceImpl loanService;

    @Mock
    LoanAmountCalc loanAmountCalc;
    @Mock
    CrediCalcStrategy crediCalcStrategy;
    @Mock
    SegmentationRepository segmentationRepository;

    String personalCode;
    BigDecimal loanAmount;
    short loanPeriod;

    LoanApplicationDto dto;

    @BeforeEach
    void init(){
        dto = new LoanApplicationDto();
        personalCode = "2";
        loanAmount = BigDecimal.valueOf(9000);
        loanPeriod = 100;
    }

    @Test
    void testLoanEligibilityWhenLoanDTOIsNUll() {
        dto = null;
        assertThrows(InvalidLoanApplicationDetailsException.class,()->{loanService.loanEligibility(dto);});
    }

    @Test
    void testLoanEligibilityWhenLoanDTOIsNotNUll() {
        dto.setPersonalCode("3");
        dto.setLoanPeriod((short)24);
        dto.setLoanAmount(BigDecimal.valueOf(8000));
        LoanResponse response = new LoanResponse();

        response.setResult(true);
        response.setMaxApprovedAmount(BigDecimal.valueOf(4800));
        response.setApprovedPeriod(BigDecimal.valueOf(24));

        Mockito.when(loanService.loanEligibility(dto)).thenReturn(response);
        var response1 = loanService.loanDecision(dto);


        System.out.println(response);
        BigDecimal maxApproved = response1.getMaxApprovedAmount();
        BigDecimal approvedPeriod = response1.getApprovedPeriod();
        assertEquals(BigDecimal.valueOf(4800),maxApproved);
        assertEquals(BigDecimal.valueOf(24),approvedPeriod);
    }
    @Test
    void testWhenLoanAmountAndPersonalCodeAreNull(){
        dto.setLoanAmount(loanAmount);
        dto.setLoanPeriod((short)100);
        dto.setPersonalCode(" ");
        assertThrows(InvalidLoanApplicationDetailsException.class,()-> loanService.loanEligibility(dto));
    }

    @Test
    void testWhenLoanPeriodIsZeroOrLess(){
        dto.setLoanAmount(loanAmount);
        dto.setLoanPeriod((short)-2);
        dto.setPersonalCode(personalCode);
        assertThrows(InvalidLoanApplicationDetailsException.class,()-> loanService.loanEligibility(dto));
    }

    @Test
    void testLoanEligibilityLoanAmountApplicationAmountIsLessThan2000() {
        dto.setPersonalCode(personalCode);
        dto.setLoanPeriod((short)20);
        dto.setLoanAmount(BigDecimal.valueOf(1900));
        assertThrows(InvalidLoanApplicationDetailsException.class,()->{loanService.loanDecision(dto);});
    }

    @Test
    void testLoanEligibilityLoanAmountApplicationAmountIsGreaterThan10000() {
        dto.setPersonalCode(personalCode);
        dto.setLoanPeriod((short)20);
        dto.setLoanAmount(BigDecimal.valueOf(1001));
        assertThrows(InvalidLoanApplicationDetailsException.class,()->{loanService.loanDecision(dto);});
    }
    @Test
    void testLoanEligibilityWhenCreditScoreIsLessThan1(){
        LoanResponse loanResponse = new LoanResponse();

        loanResponse.setResult(true);
        loanResponse.setMaxApprovedAmount(BigDecimal.valueOf(3000));
        loanResponse.setApprovedPeriod(BigDecimal.valueOf(10));

        dto.setLoanAmount(BigDecimal.valueOf(9000));
        dto.setLoanPeriod((short) 10);
        dto.setPersonalCode("4");

        Mockito.when(crediCalcStrategy.calcCreditScore(dto)).thenReturn(BigDecimal.valueOf(0.333));
        Mockito.when(loanAmountCalc.calMaxLoanAmount(dto)).thenReturn(loanResponse);

        assertEquals(BigDecimal.valueOf(3000), loanService.loanEligibility(dto).getMaxApprovedAmount());
        assertEquals(BigDecimal.valueOf(10), loanService.loanEligibility(dto).getApprovedPeriod());
    }

    @Test
    void testLoanEligibilityWhenCreditScoreIsGreaterThan1(){
        LoanResponse loanResponse = new LoanResponse();

        loanResponse.setResult(true);
        loanResponse.setMaxApprovedAmount(BigDecimal.valueOf(10000));
        loanResponse.setApprovedPeriod(BigDecimal.valueOf(33.3));

        dto.setLoanAmount(BigDecimal.valueOf(9000));
        dto.setLoanPeriod((short) 100);
        dto.setPersonalCode("4");

        Mockito.when(crediCalcStrategy.calcCreditScore(dto)).thenReturn(BigDecimal.valueOf(33.3));
        Mockito.when(loanAmountCalc.calMaxLoanAmount(dto)).thenReturn(loanResponse);

        assertEquals(BigDecimal.valueOf(10000), loanService.loanEligibility(dto).getMaxApprovedAmount());
        assertEquals(BigDecimal.valueOf(33.3), loanService.loanEligibility(dto).getApprovedPeriod());
    }
}