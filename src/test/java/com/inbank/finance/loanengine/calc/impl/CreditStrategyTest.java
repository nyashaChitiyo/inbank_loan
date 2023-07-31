package com.inbank.finance.loanengine.calc.impl;

import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.entity.Segmentation;
import com.inbank.finance.loanengine.entity.User;
import com.inbank.finance.loanengine.exception.SegmentationException;
import com.inbank.finance.loanengine.exception.UserException;
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
import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreditStrategyTest {

    @InjectMocks
    CreditStrategy creditStrategy;

    @Mock
    UserRepository userRepository;
    @Mock
    SegmentationRepository segmentationRepository;
    User user;
    LoanApplicationDto applicationDto;
    String personalCode;
    BigDecimal loanAmount;
    short loanPeriod;
    String name;
    String segmentId;

    String segmentName;
    int creditModifier;
    Segmentation segmentation;

    @BeforeEach
    void init(){
        personalCode = "2";
        loanAmount = BigDecimal.valueOf(9000);
        loanPeriod = 100;
        name = "John";
        segmentId = "49002010976";
        user = new User();
        user.setName(name);
        user.setPersonalCode(personalCode);
        user.setSegmentId(segmentId);

        applicationDto = new LoanApplicationDto();
        applicationDto.setPersonalCode(personalCode);
        applicationDto.setLoanPeriod(loanPeriod);
        applicationDto.setLoanAmount(loanAmount);

        segmentation = new Segmentation();
        segmentName = 	"segment 1";
        creditModifier = 100;
        segmentation.setCreditModifier(creditModifier);
        segmentation.setSegmentName(segmentName);
        segmentation.setSegmentId(segmentId);
    }

    @Test
    void testWhenLoanDetailsAreMissing(){
        applicationDto = null;
        Mockito.lenient().when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class,()->{creditStrategy.calcCreditScore(applicationDto);});
    }
    @Test
    void testWhenPersonalCodeDoesnotExistInDatabase(){
        personalCode = "not exist";
        applicationDto.setPersonalCode(personalCode);

        assertThrows(UserException.class,()->{
            creditStrategy.calcCreditScore(applicationDto);
        });
    }
    @Test
    void testWhenSegmentIdDoesNotExistsInDatabase(){
        applicationDto.setPersonalCode(personalCode);
        user.setSegmentId("09");
        Mockito.when(userRepository.findById(applicationDto.getPersonalCode())).thenReturn(Optional.of(user));
        assertThrows(SegmentationException.class,()->{
            creditStrategy.calcCreditScore(applicationDto);
        });
    }

    @Test
    void testWhenSegmentNameIsDebt(){
        segmentation.setSegmentId("49002010965");
        Mockito.when(segmentationRepository.findById(segmentId)).thenReturn(Optional.of(segmentation));
        Mockito.when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        assertThrows(SegmentationException.class,()->{
            creditStrategy.calcCreditScore(applicationDto);
        });
    }

    @Test
    void testCreditScoreWhenCreditModifierIs100Calculation(){
        Mockito.lenient().when(segmentationRepository.findById(segmentId)).thenReturn(Optional.of(segmentation));
        Mockito.lenient().when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        assertEquals(BigDecimal.valueOf(1.1111111111).round(new MathContext(3)),creditStrategy.calcCreditScore(applicationDto));
    }

    @Test
    void testCreditScoreWhenCreditModifierIs300Calculation(){
        segmentation.setCreditModifier(300);
        user.setSegmentId("49002010987");
        Mockito.lenient().when(segmentationRepository.findById(user.getSegmentId())).thenReturn(Optional.of(segmentation));
        Mockito.lenient().when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        assertEquals(BigDecimal.valueOf(3.333333).round(new MathContext(3)),creditStrategy.calcCreditScore(applicationDto));
    }

    @Test
    void testCreditScoreWhenCreditModifierIs1000Calculation(){
        segmentation.setCreditModifier(1000);
        user.setSegmentId("49002010998");
        Mockito.lenient().when(segmentationRepository.findById(user.getSegmentId())).thenReturn(Optional.of(segmentation));
        Mockito.lenient().when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        assertEquals(BigDecimal.valueOf(11.1111111111).round(new MathContext(3)),creditStrategy.calcCreditScore(applicationDto));
    }
}