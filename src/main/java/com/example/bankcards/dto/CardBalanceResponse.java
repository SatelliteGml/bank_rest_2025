package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardBalanceResponse {
    private BigDecimal balance;
    private String maskedCardNumber;
    private String status;
    private String currency; //Допустим по умолчанию у нас карта в BYN.
}
