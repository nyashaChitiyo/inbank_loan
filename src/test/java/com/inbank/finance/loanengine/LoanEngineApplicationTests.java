package com.inbank.finance.loanengine;

import com.inbank.finance.loanengine.dto.LoanResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanEngineApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;
	@Test
	void testWhenLoanDetailsValid() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("personalCode","3");
		jsonObject.put("loanAmount",5000);
		jsonObject.put("loanPeriod",12);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(),httpHeaders);

		ResponseEntity<LoanResponse> responseEntity = restTemplate.postForEntity("/loan",request, LoanResponse.class);
		LoanResponse response = responseEntity.getBody();
		assertEquals(HttpStatus.OK.value(),responseEntity.getStatusCode().value());

	}

}
