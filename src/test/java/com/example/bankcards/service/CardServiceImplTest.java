package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.mapper.CardMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("user1");

        card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(Status.ACTIVE);
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setIsBlocked(false);
        card.setEncryptedCardNumber(EncryptionUtil.encrypt("1234567890123456"));
    }

    @Test
    void testCreateCard_success() {
        CreateCardRequest request = new CreateCardRequest();
        request.setUserId(1L);
        request.setCardHolder("user1");
        request.setExpirationDate(LocalDate.now().plusYears(1));
        request.setInitialBalance(BigDecimal.valueOf(500));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.existsByEncryptedCardNumber(anyString())).thenReturn(false);
        when(cardRepository.save(any())).thenReturn(card);
        when(cardMapper.toDto(any())).thenReturn(new CardDto());

        CardDto result = cardService.createCard(request);

        assertNotNull(result);
        verify(cardRepository).save(any());
    }

    @Test
    void testBlockCard_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);
        when(cardMapper.toDto(any())).thenReturn(new CardDto());

        CardDto result = cardService.blockCard(1L);

        assertNotNull(result);
        assertTrue(card.getIsBlocked());
        assertEquals(Status.BLOCKED, card.getStatus());
    }

    @Test
    void testActivateCard_success() {
        card.setIsBlocked(true);
        card.setStatus(Status.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);

        CardDto dto = new CardDto();
        dto.setStatus(Status.ACTIVE);
        dto.setIsBlocked(false);
        when(cardMapper.toDto(any())).thenReturn(dto);

        CardDto result = cardService.activateCard(1L);

        assertNotNull(result);
        assertFalse(result.getIsBlocked());
        assertEquals(Status.ACTIVE, result.getStatus());
    }


    @Test
    void testGetCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.getCardById(1L));
    }
}
