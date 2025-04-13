package com.eteration.simplebanking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEvent {
    private String eventType;
    private String accountNumber;
    private double amount;
    private String approvalCode;
    private ZonedDateTime timestamp;
}
