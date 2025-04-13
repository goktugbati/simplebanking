package com.eteration.simplebanking.command;

import com.eteration.simplebanking.command.dto.CommandResponse;
import com.eteration.simplebanking.command.service.BankAccountCommandService;
import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BankAccountCommandServiceTest {

    @InjectMocks
    private BankAccountCommandService commandService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    private BankAccount mockAccount;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockAccount = new BankAccount("Jim", "12345");
        when(bankAccountRepository.findById("12345")).thenReturn(java.util.Optional.of(mockAccount));
    }

    @Test
    public void testCredit() {
        CommandResponse response = commandService.credit("12345", 100.0);

        assertEquals("OK", response.getStatus());
        assertEquals(100.0, mockAccount.getBalance());
        verify(bankAccountRepository).save(mockAccount);
    }

    @Test
    public void testDebit() {
        mockAccount.credit(200.0); // preload balance

        CommandResponse response = commandService.debit("12345", 50.0);

        assertEquals("OK", response.getStatus());
        assertEquals(150.0, mockAccount.getBalance());
        verify(bankAccountRepository).save(mockAccount);
    }

    @Test
    public void testDebit_InsufficientFunds() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            commandService.debit("12345", 50.0);
        });
        assertEquals("Insufficient funds.", ex.getMessage());
    }

    @Test
    public void testPayBill() {
        mockAccount.credit(500.0);

        CommandResponse response = commandService.payBill("12345", "Netflix", 100.0);

        assertEquals("OK", response.getStatus());
        assertEquals(400.0, mockAccount.getBalance());
        verify(bankAccountRepository).save(mockAccount);
    }

    @Test
    public void testPayBill_InsufficientFunds() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            commandService.payBill("12345", "Spotify", 100.0);
        });
        assertEquals("Insufficient funds.", ex.getMessage());
    }

    @Test
    public void testAccountNotFound() {
        when(bankAccountRepository.findById("not-found")).thenReturn(java.util.Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            commandService.credit("not-found", 100.0);
        });

        assertEquals("Account not found: not-found", ex.getMessage());
    }
}
