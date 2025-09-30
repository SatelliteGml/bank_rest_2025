package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResult;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Card fromCard;
    private Card toCard;
    private User user;
    private static final Long USER_ID = 1L;
    private static final Long FROM_CARD_ID = 1L;
    private static final Long TO_CARD_ID = 2L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);

        fromCard = new Card();
        fromCard.setId(FROM_CARD_ID);
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setStatus(Status.ACTIVE);

        toCard = new Card();
        toCard.setId(TO_CARD_ID);
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setStatus(Status.ACTIVE);
    }

    @Test
    void testTransferBetweenUserCards_success() {
        TransferRequest request = new TransferRequest();
        request.setFromCardId(FROM_CARD_ID);
        request.setToCardId(TO_CARD_ID);
        request.setAmount(BigDecimal.valueOf(200));

        when(cardRepository.findById(FROM_CARD_ID)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(TO_CARD_ID)).thenReturn(Optional.of(toCard));

        TransferResult result = transferService.transferBetweenUserCards(USER_ID, request);

        assertEquals("SUCCESS", result.getStatus());
        assertEquals(BigDecimal.valueOf(800), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());

        verify(cardRepository).findById(FROM_CARD_ID);
        verify(cardRepository).findById(TO_CARD_ID);
        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
        verify(transactionRepository).save(any());
    }

    @Test
    void testTransferBetweenUserCards_insufficientFunds() {
        TransferRequest request = new TransferRequest();
        request.setFromCardId(FROM_CARD_ID);
        request.setToCardId(TO_CARD_ID);
        request.setAmount(BigDecimal.valueOf(2000));

        when(cardRepository.findById(FROM_CARD_ID)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(TO_CARD_ID)).thenReturn(Optional.of(toCard));

        assertThrows(InsufficientFundsException.class, () ->
                transferService.transferBetweenUserCards(USER_ID, request));

        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testTransferBetweenUserCards_fromCardNotFound() {
        TransferRequest request = new TransferRequest();
        request.setFromCardId(999L);
        request.setToCardId(TO_CARD_ID);
        request.setAmount(BigDecimal.valueOf(100));

        when(cardRepository.findById(999L)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () ->
                transferService.transferBetweenUserCards(USER_ID, request));

        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}