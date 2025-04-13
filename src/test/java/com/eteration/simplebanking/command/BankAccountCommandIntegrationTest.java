package com.eteration.simplebanking.command;

import com.eteration.simplebanking.command.dto.BillPaymentRequest;
import com.eteration.simplebanking.command.dto.CommandResponse;
import com.eteration.simplebanking.command.dto.CreditRequest;
import com.eteration.simplebanking.command.dto.DebitRequest;
import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankAccountCommandIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BankAccountRepository accountRepository;

    private final String BASE_URL = "/account/v1";

    @BeforeEach
    public void resetAccount() {
        BankAccount account = accountRepository.findById("669-7788").orElseThrow();
        account.setBalance(1000.0);
        accountRepository.save(account);
    }

    @Test
    public void testCreditEndpoint() {
        CreditRequest request = new CreditRequest();
        request.setAmount(1000.0);

        ResponseEntity<CommandResponse> response = restTemplate.postForEntity(
                BASE_URL + "/credit/669-7788", request, CommandResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("OK", response.getBody().getStatus());

        BankAccount account = accountRepository.findById("669-7788").orElseThrow();
        assertEquals(1950.0, account.getBalance(), 0.0001);
    }

    @Test
    public void testDebitEndpoint() {
        DebitRequest request = new DebitRequest();
        request.setAmount(300.0);

        ResponseEntity<CommandResponse> response = restTemplate.postForEntity(
                BASE_URL + "/debit/669-7788", request, CommandResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BankAccount account = accountRepository.findById("669-7788").orElseThrow();
        assertEquals(700.0, account.getBalance(), 0.0001);
    }

    @Test
    public void testDebit_InsufficientFunds() {
        DebitRequest request = new DebitRequest();
        request.setAmount(2000.0);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/debit/669-7788", request, String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testPayBillEndpoint() {
        BillPaymentRequest request = new BillPaymentRequest();
        request.setPayee("Netflix");
        request.setAmount(100.0);

        ResponseEntity<CommandResponse> response = restTemplate.postForEntity(
                BASE_URL + "/paybill/669-7788", request, CommandResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        BankAccount account = accountRepository.findById("669-7788").orElseThrow();
        assertEquals(900.0, account.getBalance(), 0.0001);
    }
}
