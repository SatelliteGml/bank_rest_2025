package com.example.bankcards.controller;

import com.example.bankcards.dto.CardBalanceResponse;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.BalanceService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private UserCardController userCardController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userCardController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

    }

    @Test
    void testGetUserCards() throws Exception {
        CardDto dto = new CardDto();
        when(cardService.getUserCards(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/users/my").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testGetUserCardsPaginated() throws Exception {
        CardDto cardDto = new CardDto();
        Page<CardDto> page = new PageImpl<>(List.of(cardDto), PageRequest.of(0, 10), 1);

        when(cardService.getUserCardsPaginated(eq(1L), any(), eq(null)))
                .thenReturn(page);

        mockMvc.perform(get("/users/paginated")
                        .param("userId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists());
    }




    @Test
    void testGetCardBalance() throws Exception {
        when(balanceService.getCardBalance(1L)).thenReturn(BigDecimal.valueOf(100));

        mockMvc.perform(get("/users/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testGetAllBalances() throws Exception {
        CardBalanceResponse response = new CardBalanceResponse(
                BigDecimal.valueOf(100),
                "**** **** **** 1234",
                "ACTIVE",
                "BYN"
        );

        when(balanceService.getAllUserCardBalances(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/users/1/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].balance").value(100))
                .andExpect(jsonPath("$[0].maskedCardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].currency").value("BYN"));
    }

}
