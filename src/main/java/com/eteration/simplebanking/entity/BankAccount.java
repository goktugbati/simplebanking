package com.eteration.simplebanking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class BankAccount {

    @Id
    private String accountNumber;

    private String owner;
    private double balance;
    private String createDate;

    @Version
    private Long version;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String owner, String accountNumber) {
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.createDate = ZonedDateTime.now().toString();
    }

    public void post(Transaction transaction) {
        transaction.apply(this);
        transactions.add(transaction);
    }

    public void credit(double amount) {
        this.balance += amount;
    }

    public void debit(double amount) {
        this.balance -= amount;
    }
}
