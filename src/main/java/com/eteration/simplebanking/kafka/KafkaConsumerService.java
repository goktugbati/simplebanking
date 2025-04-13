package com.eteration.simplebanking.kafka;

import com.eteration.simplebanking.entity.BankAccount;
import com.eteration.simplebanking.event.AccountEvent;
import com.eteration.simplebanking.entity.*;
import com.eteration.simplebanking.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final BankAccountRepository repository;

    @KafkaListener(topics = "account-events", groupId = "banking-service-group")
    @Transactional
    public void consume(AccountEvent event) {
        log.info("📥 Consuming AccountEvent: {}", event);

        Optional<BankAccount> optional = repository.findById(event.getAccountNumber());
        if (optional.isEmpty()) {
            log.warn("Account {} not found. Skipping event.", event.getAccountNumber());
            return;
        }

        BankAccount account = optional.get();

        // Idempotency check to avoid duplicate processing
        boolean alreadyExists = account.getTransactions().stream()
            .anyMatch(tx -> tx.getApprovalCode().equals(event.getApprovalCode()));

        if (alreadyExists) {
            log.info("⚠️ Event already applied: {}", event.getApprovalCode());
            return;
        }

        Transaction tx = getTransaction(event);

        account.post(tx);
        repository.save(account);

        log.info("✅ Event applied successfully: {}", event.getApprovalCode());
    }

    private static Transaction getTransaction(AccountEvent event) {
        Transaction tx = switch (event.getEventType()) {
            case "DepositMade" -> new DepositTransaction(event.getAmount());
            case "WithdrawalMade" -> new WithdrawalTransaction(event.getAmount());
            case "BillPaymentMade" -> new BillPaymentTransaction("replicated", event.getAmount());
            default -> throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
        };

        tx.setApprovalCode(event.getApprovalCode());
        tx.setDate(event.getTimestamp().toString());
        return tx;
    }
}
