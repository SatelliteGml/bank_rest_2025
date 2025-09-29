package com.example.bankcards.service;

import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    Card createCard(CreateCardRequest request);

    Card blockCard(Long cardId);

    Card activateCard(Long cardId);

    void deleteCard(Long cardId);

    Card getCardById(Long cardId);

    Card getCardByIdAndUserId(Long cardId, Long userId);

    List<Card> getUserCards(Long userId);

    Page<Card> getUserCardsPaginated(Long userId, Pageable pageable, String search);

    void updateCardBalance(Long cardId, BigDecimal newBalance);

    void validateCardForTransaction(Long cardId, Long userId);
}
