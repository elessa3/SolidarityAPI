package com.elessa.immigrant.service;

import com.elessa.immigrant.dto.ImmigrantRequestDTO;
import com.elessa.immigrant.dto.ImmigrantResponseDTO;
import com.elessa.immigrant.enums.ImmigrantStatus;
import com.elessa.immigrant.exception.ResourceNotFoundException;
import com.elessa.immigrant.model.Immigrant;
import com.elessa.immigrant.repository.ImmigrantRepository;
import com.elessa.immigrant.repository.ServiceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImmigrantService {

    @Autowired
    private ImmigrantRepository immigrantRepository;

    @Autowired
    private ServiceRecordRepository serviceRecordRepository;

    @Transactional
    public ImmigrantResponseDTO create(ImmigrantRequestDTO requestDTO) {
        // Verificar se documento já existe (se foi fornecido)
        if (requestDTO.getDocumentNumber() != null &&
                immigrantRepository.existsByDocumentNumber(requestDTO.getDocumentNumber())) {
            throw new IllegalArgumentException("Já existe um imigrante com este número de documento");
        }

        // Converter DTO para entidade
        Immigrant immigrant = new Immigrant();
        immigrant.setFullName(requestDTO.getFullName());
        immigrant.setNationality(requestDTO.getNationality());
        immigrant.setBirthDate(requestDTO.getBirthDate());
        immigrant.setDocumentNumber(requestDTO.getDocumentNumber());
        immigrant.setStatus(requestDTO.getStatus());
        immigrant.setObservations(requestDTO.getObservations());
        // Mapeamento dos novos campos
        immigrant.setPhone(requestDTO.getPhone());
        immigrant.setStreet(requestDTO.getStreet());
        immigrant.setNumber(requestDTO.getNumber());
        immigrant.setZipCode(requestDTO.getZipCode());
        immigrant.setCity(requestDTO.getCity());
        immigrant.setCountry(requestDTO.getCountry());

        Immigrant saved = immigrantRepository.save(immigrant);
        return convertToResponseDTO(saved);
    }

    public Page<ImmigrantResponseDTO> findAll(Pageable pageable) {
        return immigrantRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public Page<ImmigrantResponseDTO> findByStatus(ImmigrantStatus status, Pageable pageable) {
        return immigrantRepository.findByStatus(status, pageable)
                .map(this::convertToResponseDTO);
    }

    public Page<ImmigrantResponseDTO> findByNationality(String nationality, Pageable pageable) {
        return immigrantRepository.findByNationality(nationality, pageable)
                .map(this::convertToResponseDTO);
    }

    public ImmigrantResponseDTO findById(Long id) {
        Immigrant immigrant = immigrantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imigrante não encontrado com ID: " + id));
        return convertToResponseDTO(immigrant);
    }

    @Transactional
    public ImmigrantResponseDTO update(Long id, ImmigrantRequestDTO requestDTO) {
        Immigrant immigrant = immigrantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imigrante não encontrado com ID: " + id));

        // Atualizar apenas campos permitidos
        immigrant.setFullName(requestDTO.getFullName());
        immigrant.setNationality(requestDTO.getNationality());
        immigrant.setBirthDate(requestDTO.getBirthDate());
        immigrant.setDocumentNumber(requestDTO.getDocumentNumber());
        immigrant.setStatus(requestDTO.getStatus());
        immigrant.setObservations(requestDTO.getObservations());
        // Atualizar novos campos
        immigrant.setPhone(requestDTO.getPhone());
        immigrant.setStreet(requestDTO.getStreet());
        immigrant.setNumber(requestDTO.getNumber());
        immigrant.setZipCode(requestDTO.getZipCode());
        immigrant.setCity(requestDTO.getCity());
        immigrant.setCountry(requestDTO.getCountry());

        Immigrant updated = immigrantRepository.save(immigrant);
        return convertToResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!immigrantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Imigrante não encontrado com ID: " + id);
        }
        immigrantRepository.deleteById(id);
    }

    @Transactional
    public ImmigrantResponseDTO updateStatus(Long id, ImmigrantStatus newStatus) {
        Immigrant immigrant = immigrantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imigrante não encontrado com ID: " + id));
        immigrant.setStatus(newStatus);
        return convertToResponseDTO(immigrantRepository.save(immigrant));
    }

    private ImmigrantResponseDTO convertToResponseDTO(Immigrant immigrant) {
        int totalRecords = serviceRecordRepository.findByImmigrantId(immigrant.getId()).size();

        return new ImmigrantResponseDTO(
                immigrant.getId(),
                immigrant.getFullName(),
                immigrant.getNationality(),
                immigrant.getBirthDate(),
                immigrant.getDocumentNumber(),
                immigrant.getStatus(),
                immigrant.getObservations(),
                immigrant.getPhone(),
                immigrant.getStreet(),
                immigrant.getNumber(),
                immigrant.getZipCode(),
                immigrant.getCity(),
                immigrant.getCountry(),
                immigrant.getRegisteredAt(),
                immigrant.getLastUpdate(),
                totalRecords
        );
    }
}