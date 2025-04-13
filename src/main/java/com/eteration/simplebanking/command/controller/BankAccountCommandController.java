package com.eteration.simplebanking.command.controller;

import com.eteration.simplebanking.command.dto.BillPaymentRequest;
import com.eteration.simplebanking.command.dto.CommandResponse;
import com.eteration.simplebanking.command.dto.CreditRequest;
import com.eteration.simplebanking.command.dto.DebitRequest;
import com.eteration.simplebanking.command.service.BankAccountCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account/v1")
@RequiredArgsConstructor
public class BankAccountCommandController {

    private final BankAccountCommandService commandService;

    @PostMapping("/credit/{accountNumber}")
    public ResponseEntity<CommandResponse> credit(
            @PathVariable String accountNumber,
            @RequestBody CreditRequest request) {
        CommandResponse response = commandService.credit(accountNumber, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/debit/{accountNumber}")
    public ResponseEntity<CommandResponse> debit(
            @PathVariable String accountNumber,
            @RequestBody DebitRequest request) {
        CommandResponse response = commandService.debit(accountNumber, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/paybill/{accountNumber}")
    public ResponseEntity<CommandResponse> payBill(
            @PathVariable String accountNumber,
            @RequestBody BillPaymentRequest request) {
        CommandResponse response = commandService.payBill(accountNumber, request.getPayee(), request.getAmount());
        return ResponseEntity.ok(response);
    }
}
