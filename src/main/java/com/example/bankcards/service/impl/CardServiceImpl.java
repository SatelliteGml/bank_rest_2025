package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.mapper.CardMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;


    @Override
    public CardDto createCard(CreateCardRequest request) {
        User user = getUserById(request.getUserId());

        String cardNumber = CardNumberGenerator.generate();
        String encryptedCardNumber = EncryptionUtil.encrypt(cardNumber);
        String cvv = CardNumberGenerator.generateCVV();

        if (cardRepository.existsByEncryptedCardNumber(encryptedCardNumber)) {
            throw new IllegalStateException("Generated card number already exists");
        }

        Card card = new Card();
        card.setEncryptedCardNumber(encryptedCardNumber);
        card.setCardHolder(request.getCardHolder());
        card.setExpirationDate(request.getExpirationDate());
        card.setBalance(request.getInitialBalance());
        card.setStatus(Status.ACTIVE);
        card.setUser(user);
        card.setCvv(EncryptionUtil.encrypt(cvv));

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    @Override
    public CardDto blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        validateCardForBlocking(card);

        card.setIsBlocked(true);

        if (card.getStatus() == Status.ACTIVE) {
            card.setStatus(Status.BLOCKED);
        }

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    @Override
    public CardDto activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        validateCardForUnblocking(card);

        card.setIsBlocked(false);

        if (isCardNotExpired(card)) {
            card.setStatus(Status.ACTIVE);
        } else {
            card.setStatus(Status.EXPIRED);
        }

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    @Override
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
        cardRepository.delete(card);

    }

    @Override
    public CardDto getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        return cardMapper.toDto(card);
    }

    @Override
    public CardDto getCardByIdAndUserId(Long cardId, Long userId) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found or access denied"));

        return cardMapper.toDto(card);
    }

    @Override
    public List<CardDto> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId).stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CardDto> getUserCardsPaginated(Long userId, Pageable pageable, String search) {
        Page<Card> cardsPage;

        if (search != null && !search.trim().isEmpty()) {
            cardsPage = cardRepository.findByUserIdAndEncryptedCardNumberContaining(userId, search, pageable);
        } else {
            cardsPage = cardRepository.findByUserId(userId, pageable);
        }

        return cardsPage.map(cardMapper::toDto);
    }

    @Override
    public void updateCardBalance(Long cardId, BigDecimal newBalance) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));

        card.setBalance(newBalance);
        cardRepository.save(card);
    }

    @Override
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(cardMapper::toDto);

    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }


    private void validateCardForBlocking(Card card) {
        if (isCardNotExpired(card)) {
            throw new IllegalStateException("Cannot block expired card with id: " + card.getId());
        }

        if (card.getStatus() == Status.BLOCKED) {
            throw new IllegalStateException("Card is already blocked with id: " + card.getId());
        }
    }

    private void validateCardForUnblocking(Card card) {
        if (isCardNotExpired(card)) {
            System.out.println("Warning: Unblocking expired card id: " + card.getId());
        }

        if (!card.getIsBlocked()) {
            throw new IllegalStateException("Card is not blocked with id: " + card.getId());
        }
    }

    private boolean isCardNotExpired(Card card) {
        LocalDate today = LocalDate.now();
        return card.getExpirationDate().isAfter(today) ||
                card.getExpirationDate().isEqual(today);
    }
}
