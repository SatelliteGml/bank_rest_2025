package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
public class AdminCardController {

    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CreateCardRequest request) {
        return null;
    }

    @GetMapping
    public ResponseEntity<Page<CardDto>> getAllCards(Pageable pageable) {
        return null;
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<CardDto> blockCard(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<CardDto> activateCard(@PathVariable Long id) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        return null;
    }
}
