package com.eteration.simplebanking.query.controller;

import com.eteration.simplebanking.query.dto.AccountView;
import com.eteration.simplebanking.query.dto.PagedTransactionView;
import com.eteration.simplebanking.query.service.BankAccountQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account/v1")
@RequiredArgsConstructor
public class BankAccountQueryController {

    private final BankAccountQueryService queryService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountView> getAccount(@PathVariable String accountNumber) {
        AccountView view = queryService.getAccountView(accountNumber);
        return ResponseEntity.ok(view);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<PagedTransactionView> getTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedTransactionView view = queryService.getTransactions(accountNumber, page, size);
        return ResponseEntity.ok(view);
    }
}
