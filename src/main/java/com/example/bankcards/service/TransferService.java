package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResult;
import com.example.bankcards.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {
    TransferResult transferBetweenOwnCards(TransferRequest request);

    boolean validateTransfer(Long userId, Long fromCardId, BigDecimal amount);

    List<Transaction> getTransactionHistory(Long cardId, Long userId);
}
