package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateComfortDto {

    @NotEmpty(message = "El nombre de la comodidad es requerido")
    @Size(min = 5, max = 200, message = "El nombre debe ser mínimo de 5 caracteres y máximo de 200")
    private String name;

    @NotEmpty(message = "El tag ícono de la comodidad es requerido")
    @Size(min = 5, max = 200, message = "El tag ícono debe ser mínimo de 5 caracteres y máximo de 200")
    private String icon;

}
