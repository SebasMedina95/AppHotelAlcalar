package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoomDto {

    @NotEmpty(message = "El nombre de la habitación es requerido")
    @Size(min = 5, max = 10, message = "El nombre debe ser mínimo de 5 caracteres y máximo de 10")
    private String name;

    @NotEmpty(message = "El estado de mantenimiento de la habitación es requerido")
    @Size(min = 5, max = 50, message = "El estado de mantenimiento debe ser mínimo de 5 caracteres y máximo de 50")
    private String maintenance;

    @NotNull(message = "El id de la temática es requerido")
    private Long thematicId;

}
