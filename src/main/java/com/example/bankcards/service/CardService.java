package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    CardDto createCard(CreateCardRequest request);

    CardDto blockCard(Long cardId);

    CardDto activateCard(Long cardId);

    void deleteCard(Long cardId);

    CardDto getCardById(Long cardId);

    CardDto getCardByIdAndUserId(Long cardId, Long userId);

    List<CardDto> getUserCards(Long userId);

    Page<CardDto> getUserCardsPaginated(Long userId, Pageable pageable, String search);

    void updateCardBalance(Long cardId, BigDecimal newBalance);

}
