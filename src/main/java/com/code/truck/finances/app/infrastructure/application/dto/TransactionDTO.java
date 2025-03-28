package com.code.truck.finances.app.infrastructure.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private UUID id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String type;
    private UUID userId;
}
