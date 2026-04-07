package com.elessa.immigrant.model;

import com.elessa.immigrant.enums.ImmigrantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "immigrants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Immigrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String nationality;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(unique = true, length = 50)
    private String documentNumber; // Número do passaporte/identidade (opcional)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status; // PENDING, REGULARIZED, DOCUMENT_ISSUE

    @Column(length = 500)
    private String observations; // Observações sobre o caso

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "immigrant", cascade = CascadeType.ALL)
    private List<ServiceRecord> serviceRecords = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}