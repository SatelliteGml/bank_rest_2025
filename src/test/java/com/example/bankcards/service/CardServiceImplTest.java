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
    private static final Long USER_ID = 1L;
    private static final Long CARD_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);
        user.setUsername("user1");

        card = new Card();
        card.setId(CARD_ID);
        card.setUser(user);
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(Status.ACTIVE);
        card.setExpirationDate(LocalDate.of(2099, 12, 31));
        card.setIsBlocked(false);
        card.setEncryptedCardNumber(EncryptionUtil.encrypt("1234567890123456"));
    }

    @Test
    void testCreateCard_success() {
        CreateCardRequest request = new CreateCardRequest();
        request.setUserId(USER_ID);
        request.setCardHolder("user1");
        request.setExpirationDate(LocalDate.now().plusYears(1));
        request.setInitialBalance(BigDecimal.valueOf(500));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(cardRepository.existsByEncryptedCardNumber(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        CardDto expectedDto = new CardDto();
        when(cardMapper.toDto(any(Card.class))).thenReturn(expectedDto);

        CardDto result = cardService.createCard(request);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(userRepository).findById(USER_ID);
        verify(cardRepository).existsByEncryptedCardNumber(anyString());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toDto(any(Card.class));
    }

    @Test
    void testCreateCard_userNotFound() {
        CreateCardRequest request = new CreateCardRequest();
        request.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.createCard(request));

        verify(userRepository).findById(999L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void testBlockCard_success() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        CardDto expectedDto = new CardDto();
        when(cardMapper.toDto(card)).thenReturn(expectedDto);

        CardServiceImpl spyService = spy(cardService);
        doNothing().when(spyService).validateCardForBlocking(card);

        CardDto result = spyService.blockCard(CARD_ID);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertTrue(card.getIsBlocked());
        assertEquals(Status.BLOCKED, card.getStatus());

        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository).save(card);
        verify(cardMapper).toDto(card);
        verify(spyService).validateCardForBlocking(card);
    }

    @Test
    void testActivateCard_success() {
        card.setIsBlocked(true);
        card.setStatus(Status.BLOCKED);

        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        CardDto dto = new CardDto();
        dto.setStatus(Status.ACTIVE);
        dto.setIsBlocked(false);
        when(cardMapper.toDto(card)).thenReturn(dto);

        CardDto result = cardService.activateCard(CARD_ID);

        assertNotNull(result);
        assertFalse(result.getIsBlocked());
        assertEquals(Status.ACTIVE, result.getStatus());
        assertFalse(card.getIsBlocked());
        assertEquals(Status.ACTIVE, card.getStatus());

        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository).save(card);
        verify(cardMapper).toDto(card);
    }

    @Test
    void testGetCardById_notFound() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.getCardById(CARD_ID));

        verify(cardRepository).findById(CARD_ID);
        verify(cardMapper, never()).toDto(any());
    }

    @Test
    void testGetCardById_success() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));

        CardDto expectedDto = new CardDto();
        when(cardMapper.toDto(card)).thenReturn(expectedDto);

        CardDto result = cardService.getCardById(CARD_ID);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(cardRepository).findById(CARD_ID);
        verify(cardMapper).toDto(card);
    }
}