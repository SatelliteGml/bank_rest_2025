package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.BalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Card card;
    private static final Long CARD_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final BigDecimal CARD_BALANCE = BigDecimal.valueOf(1000);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        card = new Card();
        card.setId(CARD_ID);
        card.setBalance(CARD_BALANCE);
    }

    @Test
    void testGetCardBalance_success() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));

        BigDecimal balance = balanceService.getCardBalance(CARD_ID);

        assertEquals(CARD_BALANCE, balance);
        verify(cardRepository).findById(CARD_ID);
    }

    @Test
    void testGetCardBalance_notFound() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> balanceService.getCardBalance(CARD_ID));
        verify(cardRepository).findById(CARD_ID);
    }

    @Test
    void testGetUserTotalBalance_success() {
        Card card2 = new Card();
        card2.setId(2L);
        card2.setBalance(BigDecimal.valueOf(1500));

        when(cardRepository.findByUserId(USER_ID)).thenReturn(List.of(card, card2));

        BigDecimal total = balanceService.getUserTotalBalance(USER_ID);

        assertEquals(BigDecimal.valueOf(2500), total);
        verify(cardRepository).findByUserId(USER_ID);
    }

    @Test
    void testGetUserTotalBalance_emptyList() {
        when(cardRepository.findByUserId(USER_ID)).thenReturn(List.of());

        BigDecimal total = balanceService.getUserTotalBalance(USER_ID);

        assertEquals(BigDecimal.ZERO, total);
        verify(cardRepository).findByUserId(USER_ID);
    }
}