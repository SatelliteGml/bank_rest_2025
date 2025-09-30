package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResult {
    private String status;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private String description;
}
