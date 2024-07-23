package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateThematicDto {

    @NotEmpty(message = "El nombre de la temática es requerido")
    @Size(min = 5, max = 200, message = "El nombre debe ser mínimo de 5 caracteres y máximo de 150")
    private String name;

    @NotEmpty(message = "La descripción de la temática es requerido")
    @Size(min = 5, max = 1000, message = "La descripción debe ser mínimo de 5 caracteres y máximo de 1000")
    private String description;

    @NotNull(message = "La lista de comodidades no puede ir vacía")
    @Size(min = 1, message = "El listado de comodidades debe contener al menos 1 elemento")
    private List<Long> comfortsListId;

}
