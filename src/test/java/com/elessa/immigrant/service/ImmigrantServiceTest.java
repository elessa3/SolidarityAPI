package com.elessa.immigrant.service;

import com.elessa.immigrant.dto.ImmigrantRequestDTO;
import com.elessa.immigrant.dto.ImmigrantResponseDTO;
import com.elessa.immigrant.enums.ImmigrantStatus;
import com.elessa.immigrant.exception.ResourceNotFoundException;
import com.elessa.immigrant.model.Immigrant;
import com.elessa.immigrant.repository.ImmigrantRepository;
import com.elessa.immigrant.repository.ServiceRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ImmigrantServiceTest {

    @Mock
    private ImmigrantRepository immigrantRepository;

    @Mock
    private ServiceRecordRepository serviceRecordRepository;

    @InjectMocks
    private ImmigrantService immigrantService;

    private Immigrant immigrant;
    private ImmigrantRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Configura dados de teste antes de cada teste
        immigrant = new Immigrant();
        immigrant.setId(1L);
        immigrant.setFullName("Maria Silva");
        immigrant.setNationality("Venezuela");
        immigrant.setBirthDate(LocalDate.of(1990, 5, 15));
        immigrant.setStatus(ImmigrantStatus.PENDING);
        immigrant.setRegisteredAt(LocalDateTime.now());
        immigrant.setLastUpdate(LocalDateTime.now());

        requestDTO = new ImmigrantRequestDTO();
        requestDTO.setFullName("Maria Silva");
        requestDTO.setNationality("Venezuela");
        requestDTO.setBirthDate(LocalDate.of(1990, 5, 15));
        requestDTO.setStatus(ImmigrantStatus.PENDING);
    }

    @Test
    void create_ShouldReturnImmigrantResponseDTO_WhenSuccessful() {
        // Arrange (preparar)
        when(immigrantRepository.save(any(Immigrant.class))).thenReturn(immigrant);
        when(serviceRecordRepository.findByImmigrantId(anyLong())).thenReturn(List.of());

        // Act (executar)
        ImmigrantResponseDTO response = immigrantService.create(requestDTO);

        // Assert (verificar)
        assertNotNull(response);
        assertEquals("Maria Silva", response.getFullName());
        assertEquals("Venezuela", response.getNationality());
        verify(immigrantRepository, times(1)).save(any(Immigrant.class));
    }

    @Test
    void findById_ShouldReturnImmigrant_WhenExists() {
        // Arrange
        when(immigrantRepository.findById(1L)).thenReturn(Optional.of(immigrant));
        when(serviceRecordRepository.findByImmigrantId(1L)).thenReturn(List.of());

        // Act
        ImmigrantResponseDTO response = immigrantService.findById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Maria Silva", response.getFullName());
    }

    @Test
    void findById_ShouldThrowException_WhenImmigrantNotFound() {
        // Arrange
        when(immigrantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            immigrantService.findById(99L);
        });
    }

    @Test
    void findAll_ShouldReturnPageOfImmigrants() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Immigrant> page = new PageImpl<>(List.of(immigrant));
        when(immigrantRepository.findAll(pageable)).thenReturn(page);
        when(serviceRecordRepository.findByImmigrantId(anyLong())).thenReturn(List.of());

        // Act
        Page<ImmigrantResponseDTO> result = immigrantService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Maria Silva", result.getContent().get(0).getFullName());
    }

    @Test
    void delete_ShouldDeleteImmigrant_WhenExists() {
        // Arrange
        when(immigrantRepository.existsById(1L)).thenReturn(true);
        doNothing().when(immigrantRepository).deleteById(1L);

        // Act
        immigrantService.delete(1L);

        // Assert
        verify(immigrantRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenImmigrantNotFound() {
        // Arrange
        when(immigrantRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            immigrantService.delete(99L);
        });
    }

    @Test
    void updateStatus_ShouldChangeStatus_WhenImmigrantExists() {
        // Arrange
        when(immigrantRepository.findById(1L)).thenReturn(Optional.of(immigrant));
        when(immigrantRepository.save(any(Immigrant.class))).thenReturn(immigrant);
        when(serviceRecordRepository.findByImmigrantId(1L)).thenReturn(List.of());

        // Act
        ImmigrantResponseDTO response = immigrantService.updateStatus(1L, ImmigrantStatus.REGULARIZED);

        // Assert
        assertEquals(ImmigrantStatus.REGULARIZED, response.getStatus());
    }
}
