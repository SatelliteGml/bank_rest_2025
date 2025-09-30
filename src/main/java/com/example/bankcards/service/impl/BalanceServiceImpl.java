package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardBalanceResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final CardRepository cardRepository;

    @Override
    public BigDecimal getCardBalance(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
        return card.getBalance();
    }

    @Override
    public CardBalanceResponse getCardBalanceWithDetails(Long cardId, Long userId) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId).orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));

        return new CardBalanceResponse(card.getBalance(), card.getEncryptedCardNumber(), card.getStatus().name(), "BYN");
    }

    @Override
    public List<CardBalanceResponse> getAllUserCardBalances(Long userId) {
        List<Card> userCards = cardRepository.findByUserId(userId);

        return userCards.stream()
                .map(card -> new CardBalanceResponse(
                        card.getBalance(),
                        card.getEncryptedCardNumber(),
                        card.getStatus().name(),
                        "BYN"
                ))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getUserTotalBalance(Long userId) {
        List<Card> userCards = cardRepository.findByUserId(userId);
        return userCards.stream()
                .map(Card::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
