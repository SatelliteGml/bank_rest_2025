package com.example.bankcards.controller;

import com.example.bankcards.dto.CardBalanceResponse;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.BalanceService;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;
    private final BalanceService balanceService;

    @GetMapping("/my")
    public ResponseEntity<List<CardDto>> getUserCards(@RequestParam Long userId) {
        return ResponseEntity.ok(cardService.getUserCards(userId));
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CardDto>> getUserCardsPaginated(
            @RequestParam Long userId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ResponseEntity.ok(cardService.getUserCardsPaginated(userId, pageable, search));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable Long id) {
        return ResponseEntity.ok(balanceService.getCardBalance(id));
    }

    @GetMapping("/{userId}/balances")
    public ResponseEntity<List<CardBalanceResponse>> getAllBalances(@PathVariable Long userId) {
        return ResponseEntity.ok(balanceService.getAllUserCardBalances(userId));
    }


}
