package com.eteration.simplebanking.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("BillPaymentTransaction")
@Getter @Setter @NoArgsConstructor
public class BillPaymentTransaction extends Transaction {

    private String payee;

    public BillPaymentTransaction(String payee, double amount) {
        super(amount);
        this.payee = payee;
    }

    @Override
    public void apply(BankAccount account) {
        account.debit(amount);
        this.bankAccount = account;
    }
}
