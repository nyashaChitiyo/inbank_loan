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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanAmountCalcImplTest {


    @InjectMocks
    LoanAmountCalcImpl loanAmountCalc;
    @Mock
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
        loanAmount = BigDecimal.valueOf(3000);
        loanPeriod = 10;
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
        assertThrows(IllegalArgumentException.class,()->{loanAmountCalc.calMaxLoanAmount(applicationDto);});
    }

    @Test
    void testWhenPersonalCodeDoesnotExistInDatabase(){
        personalCode = "not exist";
        applicationDto.setPersonalCode(personalCode);

        assertThrows(UserException.class,()->{
            loanAmountCalc.calMaxLoanAmount(applicationDto);
        });
    }

    @Test
    void testWhenSegmentIdDoesNotExistsInDatabase(){
        applicationDto.setPersonalCode(personalCode);
        user.setSegmentId("09");
        Mockito.when(userRepository.findById(applicationDto.getPersonalCode())).thenReturn(Optional.of(user));
        assertThrows(SegmentationException.class,()->{
            loanAmountCalc.calMaxLoanAmount(applicationDto);
        });
    }

    @Test
    void testCalMaxLoanAmountWhenCalculatedLoanAmountIsLessThan2000(){

        Mockito.when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        Mockito.when(segmentationRepository.findById(segmentId)).thenReturn(Optional.of(segmentation));
        assertEquals(BigDecimal.valueOf(2000),loanAmountCalc.calMaxLoanAmount(applicationDto).getMaxApprovedAmount());
    }

    @Test
    void testCalMaxLoanAmountWhenCalculatedLoanAmountIsMoreThan10000(){

        applicationDto.setLoanAmount(BigDecimal.valueOf(9000));
        applicationDto.setLoanPeriod((short) 100);
        applicationDto.setPersonalCode(String.valueOf(4));
        personalCode = "4";

        segmentation.setCreditModifier(300);
        segmentation.setSegmentId("49002010998");
        segmentation.setSegmentName("segment 3");
        segmentId = "49002010998";
        user.setSegmentId("49002010998");
        Mockito.when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        Mockito.when(segmentationRepository.findById(segmentId)).thenReturn(Optional.of(segmentation));
        assertEquals(BigDecimal.valueOf(10000),loanAmountCalc.calMaxLoanAmount(applicationDto).getMaxApprovedAmount());
    }

    @Test
    void testCalMaxLoanAmountWhenLoanPeriodNeedsToIncrease(){
        Mockito.when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        Mockito.when(segmentationRepository.findById(segmentId)).thenReturn(Optional.of(segmentation));
        assertTrue(loanPeriod < loanAmountCalc.calMaxLoanAmount(applicationDto).getApprovedPeriod().shortValueExact());
    }

    @Test
    void testCalMaxLoanAmountWhenLoanPeriodNeedsToDecrease(){
        applicationDto.setLoanAmount(BigDecimal.valueOf(9000));
        applicationDto.setLoanPeriod((short) 100);
        applicationDto.setPersonalCode(String.valueOf(4));
        personalCode = "4";

        segmentation.setCreditModifier(300);
        segmentation.setSegmentId("49002010998");
        segmentation.setSegmentName("segment 3");
        segmentId = "49002010998";
        user.setSegmentId("49002010998");
        Mockito.when(userRepository.findById(personalCode)).thenReturn(Optional.of(user));
        Mockito.when(segmentationRepository.findById(segmentId)).thenReturn(Optional.of(segmentation));
        System.out.println(loanAmountCalc.calMaxLoanAmount(applicationDto).getApprovedPeriod().doubleValue());
        assertTrue((double)applicationDto.getLoanPeriod() > loanAmountCalc.calMaxLoanAmount(applicationDto).getApprovedPeriod().doubleValue());
    }
}