package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardBalanceResponse;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public BigDecimal getCardBalance(Long cardId) {
        return null;
    }

    @Override
    public CardBalanceResponse getCardBalanceWithDetails(Long cardId, Long userId) {
        return null;
    }

    @Override
    public List<CardBalanceResponse> getAllUserCardBalances(Long userId) {
        return List.of();
    }

    @Override
    public BigDecimal getUserTotalBalance(Long userId) {
        return null;
    }
}
