package com.elessa.immigrant.service;

import com.elessa.immigrant.dto.ServiceRecordRequestDTO;
import com.elessa.immigrant.dto.ServiceRecordResponseDTO;
import com.elessa.immigrant.exception.ResourceNotFoundException;
import com.elessa.immigrant.model.Immigrant;
import com.elessa.immigrant.model.ServiceRecord;
import com.elessa.immigrant.model.User;
import com.elessa.immigrant.repository.ImmigrantRepository;
import com.elessa.immigrant.repository.ServiceRecordRepository;
import com.elessa.immigrant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRecordServiceTest {

    @Mock
    private ServiceRecordRepository serviceRecordRepository;

    @Mock
    private ImmigrantRepository immigrantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ServiceRecordService serviceRecordService;

    private ServiceRecord serviceRecord;
    private Immigrant immigrant;
    private User user;
    private ServiceRecordRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        immigrant = new Immigrant();
        immigrant.setId(1L);
        immigrant.setFullName("Maria Silva");

        user = new User();
        user.setId(1L);
        user.setName("Ana Voluntária");
        user.setEmail("ana@voluntaria.com");

        serviceRecord = new ServiceRecord();
        serviceRecord.setId(1L);
        serviceRecord.setDescription("Atendimento inicial - orientação sobre documentação");
        serviceRecord.setImmigrant(immigrant);
        serviceRecord.setUser(user);
        serviceRecord.setServiceDate(LocalDateTime.now());

        requestDTO = new ServiceRecordRequestDTO();
        requestDTO.setImmigrantId(1L);
        requestDTO.setDescription("Atendimento inicial - orientação sobre documentação");
    }

    // Método auxiliar para configurar o SecurityContext com um email específico
    private void mockSecurityContext(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(email);
    }

    @Test
    void create_ShouldReturnServiceRecordResponseDTO_WhenSuccessful() {
        // Arrange
        mockSecurityContext("ana@voluntaria.com");
        when(immigrantRepository.findById(1L)).thenReturn(Optional.of(immigrant));
        when(userRepository.findByEmail("ana@voluntaria.com")).thenReturn(Optional.of(user));
        when(serviceRecordRepository.save(any(ServiceRecord.class))).thenReturn(serviceRecord);

        // Act
        ServiceRecordResponseDTO response = serviceRecordService.create(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Atendimento inicial - orientação sobre documentação", response.getDescription());
        assertEquals("Ana Voluntária", response.getVolunteerName());
        assertEquals("Maria Silva", response.getImmigrantName());
        verify(serviceRecordRepository, times(1)).save(any(ServiceRecord.class));
    }

    @Test
    void create_ShouldThrowException_WhenImmigrantNotFound() {
        // Arrange
        mockSecurityContext("ana@voluntaria.com");
        when(immigrantRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setImmigrantId(99L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            serviceRecordService.create(requestDTO);
        });
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        mockSecurityContext("usuario@inexistente.com");
        when(immigrantRepository.findById(1L)).thenReturn(Optional.of(immigrant));
        when(userRepository.findByEmail("usuario@inexistente.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            serviceRecordService.create(requestDTO);
        });
    }

    @Test
    void findByImmigrant_ShouldReturnListOfRecords() {
        // Arrange
        when(immigrantRepository.existsById(1L)).thenReturn(true);
        when(serviceRecordRepository.findByImmigrantIdOrderByServiceDateDesc(1L))
                .thenReturn(List.of(serviceRecord));

        // Act
        List<ServiceRecordResponseDTO> result = serviceRecordService.findByImmigrant(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Atendimento inicial - orientação sobre documentação", result.get(0).getDescription());
    }

    @Test
    void findByImmigrant_ShouldThrowException_WhenImmigrantNotFound() {
        // Arrange
        when(immigrantRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            serviceRecordService.findByImmigrant(99L);
        });
    }

    @Test
    void findById_ShouldReturnRecord_WhenExists() {
        // Arrange
        when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

        // Act
        ServiceRecordResponseDTO response = serviceRecordService.findById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Atendimento inicial - orientação sobre documentação", response.getDescription());
    }

    @Test
    void findById_ShouldThrowException_WhenRecordNotFound() {
        // Arrange
        when(serviceRecordRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            serviceRecordService.findById(99L);
        });
    }
}