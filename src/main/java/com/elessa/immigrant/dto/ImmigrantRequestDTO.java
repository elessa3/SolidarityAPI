package com.elessa.immigrant.dto;

import com.elessa.immigrant.enums.ImmigrantStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImmigrantRequestDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String fullName;

    @NotBlank(message = "Nacionalidade é obrigatória")
    @Size(min = 2, max = 50, message = "Nacionalidade deve ter entre 2 e 50 caracteres")
    private String nationality;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;

    @Size(max = 50, message = "Número do documento deve ter no máximo 50 caracteres")
    private String documentNumber;

    private ImmigrantStatus status = ImmigrantStatus.PENDING; // Valor padrão

    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observations;

    //Info adicionais
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Pattern(regexp = "^\\+?[0-9\\s-]+$", message = "Telefone deve conter apenas números, espaços, hífen e opcionalmente + no início")
    private String phone;

    @Size(max = 100, message = "Logradouro deve ter no máximo 100 caracteres")
    private String street;

    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    private String number;

    @Size(max = 20, message = "CEP deve ter no máximo 20 caracteres")
    private String zipCode;

    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    private String city;

    @Size(max = 50, message = "País deve ter no máximo 50 caracteres")
    private String country;
}