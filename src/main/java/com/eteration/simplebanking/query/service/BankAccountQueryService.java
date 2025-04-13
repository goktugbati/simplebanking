package com.eteration.simplebanking.query.service;

import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.entity.Transaction;
import com.eteration.simplebanking.query.dto.AccountView;
import com.eteration.simplebanking.query.dto.TransactionView;
import com.eteration.simplebanking.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountQueryService {

    private final BankAccountRepository accountRepository;

    public AccountView getAccountView(String accountNumber) {
        BankAccount account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        List<TransactionView> transactions = account.getTransactions().stream()
                .map(this::mapToTransactionView)
                .collect(Collectors.toList());

        return new AccountView(
                account.getAccountNumber(),
                account.getOwner(),
                account.getBalance(),
                account.getCreateDate(),
                transactions
        );
    }

    private TransactionView mapToTransactionView(Transaction tx) {
        return new TransactionView(
                tx.getDate(),
                tx.getAmount(),
                tx.getType(),
                tx.getApprovalCode()
        );
    }
}
