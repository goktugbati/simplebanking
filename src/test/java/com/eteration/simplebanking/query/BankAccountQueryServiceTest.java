package com.eteration.simplebanking.query;

import com.eteration.simplebanking.entity.*;
import com.eteration.simplebanking.query.dto.AccountView;
import com.eteration.simplebanking.query.service.BankAccountQueryService;
import com.eteration.simplebanking.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BankAccountQueryServiceTest {

    @Mock
    private BankAccountRepository accountRepository;

    @InjectMocks
    private BankAccountQueryService queryService;

    private BankAccount account;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        account = new BankAccount("Jim", "12345");
        account.post(new DepositTransaction(1000));
        account.post(new WithdrawalTransaction(200));
        account.post(new BillPaymentTransaction("Spotify", 96.50));
    }

    @Test
    public void testGetAccountView() {
        when(accountRepository.findById("12345")).thenReturn(Optional.of(account));

        AccountView view = queryService.getAccountView("12345");

        assertEquals("12345", view.getAccountNumber());
        assertEquals("Jim", view.getOwner());
        assertEquals(703.50, view.getBalance(), 0.0001); // 1000 - 200 - 96.50
        assertEquals(3, view.getTransactions().size());
    }

    @Test
    public void testAccountNotFoundThrowsException() {
        when(accountRepository.findById("not-found")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                queryService.getAccountView("not-found"));

        assertEquals("Account not found: not-found", ex.getMessage());
    }
}
