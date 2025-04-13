package com.eteration.simplebanking.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionView {
    private String date;
    private double amount;
    private String type;
    private String approvalCode;
}
