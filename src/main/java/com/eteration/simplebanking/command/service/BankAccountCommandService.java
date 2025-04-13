package com.eteration.simplebanking.command.service;

import com.eteration.simplebanking.command.dto.CommandResponse;
import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.entity.BillPaymentTransaction;
import com.eteration.simplebanking.entity.DepositTransaction;
import com.eteration.simplebanking.entity.WithdrawalTransaction;
import com.eteration.simplebanking.event.AccountEvent;
import com.eteration.simplebanking.outbox.OutboxEvent;
import com.eteration.simplebanking.outbox.OutboxEventRepository;
import com.eteration.simplebanking.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountCommandService {
    private final BankAccountRepository accountRepository;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public CommandResponse credit(String accountNumber, double amount) {
        BankAccount account = getAccountOrThrow(accountNumber);
        return applyDeposit(account, amount, UUID.randomUUID().toString());
    }

    @Transactional
    public CommandResponse debit(String accountNumber, double amount) {
        BankAccount account = getAccountOrThrow(accountNumber);
        return applyWithdrawal(account, amount, UUID.randomUUID().toString());
    }

    @Transactional
    public CommandResponse payBill(String accountNumber, String payee, double amount) {
        BankAccount account = getAccountOrThrow(accountNumber);
        return applyBillPayment(account, payee, amount, UUID.randomUUID().toString());
    }


    public CommandResponse applyDeposit(BankAccount account, double amount, String approvalCode) {
        DepositTransaction tx = new DepositTransaction(amount);
        tx.setApprovalCode(approvalCode);
        account.post(tx);
        accountRepository.save(account);

        outboxEventRepository.save(OutboxEvent.builder()
                .eventType("DepositMade")
                .accountNumber(account.getAccountNumber())
                .amount(amount)
                .approvalCode(approvalCode)
                .timestamp(ZonedDateTime.now())
                .published(false)
                .build());

        return new CommandResponse("OK", approvalCode);
    }

    public CommandResponse applyWithdrawal(BankAccount account, double amount, String approvalCode) {
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        WithdrawalTransaction tx = new WithdrawalTransaction(amount);
        tx.setApprovalCode(approvalCode);
        account.post(tx);
        accountRepository.save(account);

        outboxEventRepository.save(OutboxEvent.builder()
                .eventType("WithdrawalMade")
                .accountNumber(account.getAccountNumber())
                .amount(amount)
                .approvalCode(approvalCode)
                .timestamp(ZonedDateTime.now())
                .published(false)
                .build());

        return new CommandResponse("OK", approvalCode);
    }

    public CommandResponse applyBillPayment(BankAccount account, String payee, double amount, String approvalCode) {
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        BillPaymentTransaction tx = new BillPaymentTransaction(payee, amount);
        tx.setApprovalCode(approvalCode);
        account.post(tx);
        accountRepository.save(account);

        outboxEventRepository.save(OutboxEvent.builder()
                .eventType("BillPaymentMade")
                .accountNumber(account.getAccountNumber())
                .amount(amount)
                .approvalCode(approvalCode)
                .timestamp(ZonedDateTime.now())
                .published(false)
                .build());

        return new CommandResponse("OK", approvalCode);
    }


    private BankAccount getAccountOrThrow(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }
}
