package com.inbank.finance.loanengine.controller;

import com.inbank.finance.loanengine.dto.LoanApplicationDto;
import com.inbank.finance.loanengine.dto.LoanResponse;
import com.inbank.finance.loanengine.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/loan")
@RestController
public class LoanController {

    private LoanService loanService;
    private static final Logger LOG = LoggerFactory.getLogger(LoanController.class);

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<LoanResponse> applyLoan(@RequestBody @Valid LoanApplicationDto applicationDto){
        LOG.info("loan request user details" ,applicationDto);
        return new ResponseEntity<>(loanService.loanDecision(applicationDto), HttpStatus.OK);
    }
}
