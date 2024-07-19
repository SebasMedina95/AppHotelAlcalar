package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Entity
@Table(name = "DTL_THEMATIC_COMFORT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailThematicComfort {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Comment("Clave primaria")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_THEMATIC")
    @JsonBackReference
    @Comment("Temática relacionada")
    private Thematic thematic;

    @ManyToOne
    @JoinColumn(name = "FK_COMFORT")
    @JsonBackReference
    @Comment("Comodidad relacionada")
    private Comfort comfort;

    @Column(name = "USER_CREATED", nullable = true, length = 100 )
    @Comment("Usuario que creó el detalle")
    private String userCreated;

    @Column(name = "DATE_CREATED", nullable = true )
    @Comment("Fecha creación el detalle")
    private Date dateCreated;

}
