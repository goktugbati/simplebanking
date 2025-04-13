package com.eteration.simplebanking.query.service;

import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.entity.Transaction;
import com.eteration.simplebanking.query.dto.AccountView;
import com.eteration.simplebanking.query.dto.PagedTransactionView;
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

    public PagedTransactionView getTransactions(String accountNumber, int page, int size) {
        BankAccount account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        List<Transaction> allTransactions = account.getTransactions();

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allTransactions.size());

        if (fromIndex > allTransactions.size()) {
            fromIndex = toIndex = 0;
        }

        List<TransactionView> transactionViews = allTransactions.subList(fromIndex, toIndex).stream()
                .map(tx -> new TransactionView(tx.getDate(), tx.getAmount(), tx.getType(), tx.getApprovalCode()))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) allTransactions.size() / size);

        return new PagedTransactionView(
                accountNumber,
                transactionViews,
                page,
                size,
                totalPages,
                allTransactions.size()
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
