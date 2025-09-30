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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BalanceServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        card = new Card();
        card.setId(1L);
        card.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void testGetCardBalance_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        BigDecimal balance = balanceService.getCardBalance(1L);

        assertEquals(BigDecimal.valueOf(1000), balance);
    }

    @Test
    void testGetCardBalance_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> balanceService.getCardBalance(1L));
    }

    @Test
    void testGetUserTotalBalance_success() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of(card, card));

        BigDecimal total = balanceService.getUserTotalBalance(1L);

        assertEquals(BigDecimal.valueOf(2000), total);
    }
}
