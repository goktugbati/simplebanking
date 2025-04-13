package com.eteration.simplebanking.command.dto;

import lombok.Data;

@Data
public class BillPaymentRequest {
    private String payee;
    private double amount;
}
