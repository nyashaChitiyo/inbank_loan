package com.inbank.finance.loanengine.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;
import com.inbank.finance.loanengine.service.LoanService;
import com.inbank.finance.loanengine.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = LoanController.class,
excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@MockBean({LoanServiceImpl.class})
class LoanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    LoanService loanService;
    @Test
    void testLoanControllerWhenLoanDetailsAreValid() throws Exception {
        LoanApplicationDto dto = new LoanApplicationDto();
        dto.setPersonalCode("3");
        dto.setLoanAmount(BigDecimal.valueOf(5000));
        dto.setLoanPeriod((short) 12);

        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setApprovedPeriod(BigDecimal.valueOf(24));
        loanResponse.setMaxApprovedAmount(BigDecimal.valueOf(4800));
        loanResponse.setResult(true);
        Mockito.when(loanService.loanDecision(dto)).thenReturn(loanResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String str = mvcResult.getResponse().getContentAsString();
        LoanResponse response = new ObjectMapper().readValue(str,LoanResponse.class);
        assertEquals(BigDecimal.valueOf(4800),response.getMaxApprovedAmount());
        assertEquals(BigDecimal.valueOf(24),response.getApprovedPeriod());
    }

    @Test
    void testLoanControllerWhenLoanDetailsAreInvalid() throws Exception {
        LoanApplicationDto dto = new LoanApplicationDto();
        dto.setPersonalCode("3");
        dto.setLoanAmount(BigDecimal.valueOf(5000));

        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setApprovedPeriod(BigDecimal.valueOf(24));
        loanResponse.setMaxApprovedAmount(BigDecimal.valueOf(4800));
        loanResponse.setResult(true);
        Mockito.when(loanService.loanDecision(dto)).thenReturn(loanResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(),mvcResult.getResponse().getStatus());
    }
}