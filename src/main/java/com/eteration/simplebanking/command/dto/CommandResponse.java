package com.eteration.simplebanking.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandResponse {
    private String status;
    private String approvalCode;
}
