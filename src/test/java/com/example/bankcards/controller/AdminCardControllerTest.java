package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private AdminCardController adminCardController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(adminCardController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testCreateCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest();
        request.setUserId(1L);
        request.setCardHolder("testData");
        request.setExpirationDate(LocalDate.now().plusYears(3));

        CardDto dto = new CardDto();
        when(cardService.createCard(any())).thenReturn(dto);

        mockMvc.perform(post("/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void testGetAllCards() throws Exception {
        CardDto dto = new CardDto();

        // Используем PageImpl с контентом и PageRequest, чтобы Jackson смог сериализовать
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<CardDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(cardService.getAllCards(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists());
    }

    @Test
    void testGetCardById() throws Exception {
        CardDto dto = new CardDto();
        when(cardService.getCardById(1L)).thenReturn(dto);

        mockMvc.perform(get("/admin/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void testBlockCard() throws Exception {
        CardDto dto = new CardDto();
        when(cardService.blockCard(1L)).thenReturn(dto);

        mockMvc.perform(put("/admin/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void testActivateCard() throws Exception {
        CardDto dto = new CardDto();
        when(cardService.activateCard(1L)).thenReturn(dto);

        mockMvc.perform(put("/admin/cards/1/activate"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void testDeleteCard() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/admin/cards/1"))
                .andExpect(status().isNoContent());
    }
}
