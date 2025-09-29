package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private String maskedCardNumber;
    private String cardHolder;
    private LocalDate expirationDate;
    private Status status;
    private BigDecimal balance;
}
