package com.elessa.immigrant.controller;

import com.elessa.immigrant.config.JwtAuthenticationFilter;
import com.elessa.immigrant.dto.ServiceRecordRequestDTO;
import com.elessa.immigrant.dto.ServiceRecordResponseDTO;
import com.elessa.immigrant.security.JwtUtil;
import com.elessa.immigrant.service.ServiceRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceRecordService serviceRecordService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ServiceRecordResponseDTO serviceRecordResponseDTO;
    private ServiceRecordRequestDTO serviceRecordRequestDTO;

    @BeforeEach
    void setUp() {
        serviceRecordResponseDTO = new ServiceRecordResponseDTO();
        serviceRecordResponseDTO.setId(1L);
        serviceRecordResponseDTO.setDescription("Atendimento inicial - orientação sobre documentação");
        serviceRecordResponseDTO.setServiceDate(LocalDateTime.now());
        serviceRecordResponseDTO.setVolunteerName("Ana Voluntária");
        serviceRecordResponseDTO.setImmigrantName("Maria Silva");
        serviceRecordResponseDTO.setImmigrantId(1L);

        serviceRecordRequestDTO = new ServiceRecordRequestDTO();
        serviceRecordRequestDTO.setImmigrantId(1L);
        serviceRecordRequestDTO.setDescription("Atendimento inicial - orientação sobre documentação");
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void create_ShouldReturnCreated_WhenVolunteerUser() throws Exception {
        when(serviceRecordService.create(any(ServiceRecordRequestDTO.class))).thenReturn(serviceRecordResponseDTO);

        mockMvc.perform(post("/api/service-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceRecordRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Atendimento inicial - orientação sobre documentação"))
                .andExpect(jsonPath("$.volunteerName").value("Ana Voluntária"));

        verify(serviceRecordService, times(1)).create(any(ServiceRecordRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnCreated_WhenAdminUser() throws Exception {
        when(serviceRecordService.create(any(ServiceRecordRequestDTO.class))).thenReturn(serviceRecordResponseDTO);

        mockMvc.perform(post("/api/service-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceRecordRequestDTO)))
                .andExpect(status().isCreated());

        verify(serviceRecordService, times(1)).create(any(ServiceRecordRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void create_ShouldReturnBadRequest_WhenDescriptionIsEmpty() throws Exception {
        serviceRecordRequestDTO.setDescription("");

        mockMvc.perform(post("/api/service-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceRecordRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(serviceRecordService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void findByImmigrant_ShouldReturnListOfRecords() throws Exception {
        // Arrange - usando anyLong() para garantir o match
        List<ServiceRecordResponseDTO> mockList = List.of(serviceRecordResponseDTO);
        when(serviceRecordService.findByImmigrant(anyLong())).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/service-records/immigrant/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Atendimento inicial - orientação sobre documentação"))
                .andExpect(jsonPath("$[0].volunteerName").value("Ana Voluntária"))
                .andExpect(jsonPath("$[0].immigrantName").value("Maria Silva"));

        verify(serviceRecordService, times(1)).findByImmigrant(anyLong());
    }

    @Test
    @WithMockUser(roles = "VOLUNTEER")
    void findById_ShouldReturnRecord_WhenExists() throws Exception {
        when(serviceRecordService.findById(anyLong())).thenReturn(serviceRecordResponseDTO);

        mockMvc.perform(get("/api/service-records/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Atendimento inicial - orientação sobre documentação"));

        verify(serviceRecordService, times(1)).findById(anyLong());
    }

    @Test
    void create_ShouldReturnUnauthorized_WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/service-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceRecordRequestDTO)))
                .andExpect(status().isUnauthorized());

        verify(serviceRecordService, never()).create(any());
    }
}