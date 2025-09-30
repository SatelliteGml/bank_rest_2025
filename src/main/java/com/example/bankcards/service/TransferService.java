package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResult;

public interface TransferService {
    TransferResult transferBetweenUserCards(Long userId, TransferRequest request);
    TransferResult transferBetweenCards(TransferRequest request);
}
