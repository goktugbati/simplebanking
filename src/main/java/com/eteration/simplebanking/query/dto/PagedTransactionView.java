package com.eteration.simplebanking.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedTransactionView {
    private String accountNumber;
    private List<TransactionView> transactions;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}
