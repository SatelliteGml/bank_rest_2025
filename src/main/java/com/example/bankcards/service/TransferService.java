package com.example.bankcards.service;

import java.math.BigDecimal;

public interface TransferService {
    TransferResult transferBetweenOwnCards(TransferRequest request);

    boolean validateTransfer(Long userId, Long fromCardId, BigDecimal amount);

    List<Transaction> getTransactionHistory(Long cardId, Long userId);
}
