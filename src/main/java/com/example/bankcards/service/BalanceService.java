package com.example.bankcards.service;

import com.example.bankcards.dto.CardBalanceResponse;

import java.math.BigDecimal;

public interface BalanceService {
    BigDecimal getCardBalance(Long cardId);

    CardBalanceResponse getCardBalanceWithDetails(Long cardId, Long userId);

    List<CardBalanceResponse> getAllUserCardBalances(Long userId);

    BigDecimal getUserTotalBalance(Long userId);
}
