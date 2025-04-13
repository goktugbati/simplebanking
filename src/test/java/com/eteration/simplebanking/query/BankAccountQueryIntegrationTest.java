package com.eteration.simplebanking.query;

import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.repository.BankAccountRepository;
import com.eteration.simplebanking.query.dto.AccountView;
import com.eteration.simplebanking.query.dto.PagedTransactionView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankAccountQueryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BankAccountRepository repository;

    private final String baseUrl = "/account/v1";

    @BeforeEach
    public void setup() {
        BankAccount account = repository.findById("669-7788").orElseThrow();
        account.setBalance(1000.0);
        repository.save(account);
    }

    @Test
    public void testGetAccountSuccess() {
        ResponseEntity<AccountView> response = restTemplate.getForEntity(
                baseUrl + "/669-7788", AccountView.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("669-7788", response.getBody().getAccountNumber());
        assertEquals(1000.0, response.getBody().getBalance(), 0.0001);
    }

    @Test
    public void testGetAccountNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/not-found", String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testPaginatedTransactionQuery() {
        ResponseEntity<PagedTransactionView> response = restTemplate.exchange(
                "/account/v1/669-7788/transactions?page=0&size=2",
                HttpMethod.GET,
                null,
                PagedTransactionView.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("669-7788", response.getBody().getAccountNumber());
        assertEquals(0, response.getBody().getPage());
        assertEquals(2, response.getBody().getSize());
    }
}
