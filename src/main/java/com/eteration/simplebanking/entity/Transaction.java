package com.eteration.simplebanking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Getter @Setter @NoArgsConstructor
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected double amount;
    protected String date;
    protected String approvalCode;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    protected BankAccount bankAccount;

    public Transaction(double amount) {
        this.amount = amount;
        this.date = ZonedDateTime.now().toString();
        this.approvalCode = UUID.randomUUID().toString();
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public abstract void apply(BankAccount account);
}
