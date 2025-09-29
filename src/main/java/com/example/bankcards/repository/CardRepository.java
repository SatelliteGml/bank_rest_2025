package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Long userId);

    List<Card> findByUserIdAndStatus(Long userId, Status status);

    Optional<Card> findByEncryptedCardNumber(String number);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    Page<Card> findByUserIdAndEncryptedCardNumberContaining(Long userId, String search, Pageable pageable);

    Optional<Card> findByIdAndUserId(Long id, Long userId);

    List<Card> findByUserIdAndIdIn(Long userId, List<Long> cardIds);

    boolean existsByEncryptedCardNumber(String number);
}

