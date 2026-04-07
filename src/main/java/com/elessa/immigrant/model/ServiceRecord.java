package com.elessa.immigrant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000, columnDefinition = "TEXT")
    private String description; // Descrição do atendimento

    @Column(name = "service_date", nullable = false)
    private LocalDateTime serviceDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Voluntário/Admin que realizou o atendimento

    @ManyToOne
    @JoinColumn(name = "immigrant_id", nullable = false)
    private Immigrant immigrant; // Imigrante atendido

    @PrePersist
    protected void onCreate() {
        serviceDate = LocalDateTime.now();
    }
}
