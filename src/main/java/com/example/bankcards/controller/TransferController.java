package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResult;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
@Validated
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/own")
    public ResponseEntity<TransferResult> transferBetweenOwnCards(
            @RequestParam Long userId,
            @Valid @RequestBody TransferRequest request
    ) {
        TransferResult result = transferService.transferBetweenUserCards(userId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/external")
    public ResponseEntity<TransferResult> transferBetweenCards(@Valid @RequestBody TransferRequest request) {
        TransferResult result = transferService.transferBetweenCards(request);
        return ResponseEntity.ok(result);
    }
}
