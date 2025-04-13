package com.eteration.simplebanking.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AccountView {
    private String accountNumber;
    private String owner;
    private double balance;
    private String createDate;
    private List<TransactionView> transactions;
}
