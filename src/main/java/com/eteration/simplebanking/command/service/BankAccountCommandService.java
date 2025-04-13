package com.eteration.simplebanking.command.service;

import com.eteration.simplebanking.command.dto.CommandResponse;
import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.entity.BillPaymentTransaction;
import com.eteration.simplebanking.entity.DepositTransaction;
import com.eteration.simplebanking.entity.WithdrawalTransaction;
import com.eteration.simplebanking.repository.BankAccountRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAccountCommandService {

    private final BankAccountRepository accountRepository;

    @Transactional
    public CommandResponse credit(String accountNumber, double amount) {
        BankAccount account = getAccountOrThrow(accountNumber);
        DepositTransaction tx = new DepositTransaction(amount);
        account.post(tx);

        try {
            accountRepository.save(account);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Concurrent update detected. Please retry.");
        }

        return new CommandResponse("OK", tx.getApprovalCode());
    }

    @Transactional
    public CommandResponse debit(String accountNumber, double amount) {
        BankAccount account = getAccountOrThrow(accountNumber);

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        WithdrawalTransaction tx = new WithdrawalTransaction(amount);
        account.post(tx);

        try {
            accountRepository.save(account);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Concurrent update detected. Please retry.");
        }

        return new CommandResponse("OK", tx.getApprovalCode());
    }

    @Transactional
    public CommandResponse payBill(String accountNumber, String payee, double amount) {
        BankAccount account = getAccountOrThrow(accountNumber);

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        BillPaymentTransaction tx = new BillPaymentTransaction(payee, amount);
        account.post(tx);

        try {
            accountRepository.save(account);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Concurrent update detected. Please retry.");
        }

        return new CommandResponse("OK", tx.getApprovalCode());
    }

    private BankAccount getAccountOrThrow(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }
}
