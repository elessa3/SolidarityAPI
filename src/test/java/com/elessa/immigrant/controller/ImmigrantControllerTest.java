package com.elessa.immigrant.controller;

import com.elessa.immigrant.config.JwtAuthenticationFilter;
import com.elessa.immigrant.dto.ImmigrantRequestDTO;
import com.elessa.immigrant.dto.ImmigrantResponseDTO;
import com.elessa.immigrant.enums.ImmigrantStatus;
import com.elessa.immigrant.security.JwtUtil;
import com.elessa.immigrant.service.ImmigrantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImmigrantController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImmigrantControllerTest {

    @Autowired
    private MockMvc mockMvc;  // Simula requisições HTTP

    @Autowired
    private ObjectMapper objectMapper;  // Converte objetos para JSON

    @MockBean
    private ImmigrantService immigrantService;  // Mock do service

    // ⬇️⬇️⬇️ Atencao ESTAS DUAS LINHAS ⬇️⬇️⬇️
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    // ⬆️⬆️⬆️ Atencao ESTAS DUAS LINHAS ⬆️⬆️⬆️


    private ImmigrantResponseDTO immigrantResponseDTO;
    private ImmigrantRequestDTO immigrantRequestDTO;

    @BeforeEach
    void setUp() {
        immigrantResponseDTO = new ImmigrantResponseDTO();
        immigrantResponseDTO.setId(1L);
        immigrantResponseDTO.setFullName("Maria Silva");
        immigrantResponseDTO.setNationality("Venezuela");
        immigrantResponseDTO.setBirthDate(LocalDate.of(1990, 5, 15));
        immigrantResponseDTO.setStatus(ImmigrantStatus.PENDING);
        immigrantResponseDTO.setRegisteredAt(LocalDateTime.now());
        immigrantResponseDTO.setLastUpdate(LocalDateTime.now());
        immigrantResponseDTO.setTotalServiceRecords(0);

        immigrantRequestDTO = new ImmigrantRequestDTO();
        immigrantRequestDTO.setFullName("Maria Silva");
        immigrantRequestDTO.setNationality("Venezuela");
        immigrantRequestDTO.setBirthDate(LocalDate.of(1990, 5, 15));
        immigrantRequestDTO.setStatus(ImmigrantStatus.PENDING);
    }

    @Test
    @WithMockUser(roles = "ADMIN")  // Simula um usuário ADMIN logado
    void create_ShouldReturnCreated_WhenAdminUser() throws Exception {
        // Arrange
        when(immigrantService.create(any(ImmigrantRequestDTO.class))).thenReturn(immigrantResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/immigrants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(immigrantRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Maria Silva"))
                .andExpect(jsonPath("$.nationality").value("Venezuela"));

        verify(immigrantService, times(1)).create(any(ImmigrantRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void create_ShouldReturnForbidden_WhenVolunteerUser() throws Exception {
        // Act & Assert - VOLUNTEER não pode criar imigrante
        mockMvc.perform(post("/api/immigrants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(immigrantRequestDTO)))
                .andExpect(status().isForbidden());  // 403

        verify(immigrantService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_ShouldReturnPageOfImmigrants() throws Exception {
        // Arrange
        Page<ImmigrantResponseDTO> page = new PageImpl<>(List.of(immigrantResponseDTO));
        when(immigrantService.findAll(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/immigrants")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].fullName").value("Maria Silva"));

        verify(immigrantService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void findAll_ShouldReturnOk_WhenVolunteerUser() throws Exception {
        // Arrange - VOLUNTEER pode ver a lista
        Page<ImmigrantResponseDTO> page = new PageImpl<>(List.of(immigrantResponseDTO));
        when(immigrantService.findAll(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/immigrants"))
                .andExpect(status().isOk());

        verify(immigrantService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ShouldReturnImmigrant_WhenExists() throws Exception {
        // Arrange
        when(immigrantService.findById(1L)).thenReturn(immigrantResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/immigrants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Maria Silva"));

        verify(immigrantService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdatedImmigrant_WhenAdminUser() throws Exception {
        // Arrange
        when(immigrantService.update(eq(1L), any(ImmigrantRequestDTO.class))).thenReturn(immigrantResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/immigrants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(immigrantRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(immigrantService, times(1)).update(eq(1L), any(ImmigrantRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturnNoContent_WhenAdminUser() throws Exception {
        // Arrange
        doNothing().when(immigrantService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/immigrants/1"))
                .andExpect(status().isNoContent());  // 204

        verify(immigrantService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void delete_ShouldReturnForbidden_WhenVolunteerUser() throws Exception {
        // Act & Assert - VOLUNTEER não pode deletar
        mockMvc.perform(delete("/api/immigrants/1"))
                .andExpect(status().isForbidden());

        verify(immigrantService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStatus_ShouldReturnUpdatedStatus() throws Exception {
        // Arrange
        immigrantResponseDTO.setStatus(ImmigrantStatus.REGULARIZED);
        when(immigrantService.updateStatus(eq(1L), eq(ImmigrantStatus.REGULARIZED)))
                .thenReturn(immigrantResponseDTO);

        // Act & Assert
        mockMvc.perform(patch("/api/immigrants/1/status")
                        .param("status", "REGULARIZED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REGULARIZED"));

        verify(immigrantService, times(1)).updateStatus(eq(1L), eq(ImmigrantStatus.REGULARIZED));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange - Dados inválidos (nome vazio)
        immigrantRequestDTO.setFullName("");

        // Act & Assert
        mockMvc.perform(post("/api/immigrants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(immigrantRequestDTO)))
                .andExpect(status().isBadRequest());  // 400 - validação falhou

        verify(immigrantService, never()).create(any());
    }
}