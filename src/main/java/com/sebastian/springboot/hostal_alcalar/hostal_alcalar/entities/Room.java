package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Entity
@Table(name = "TBL_ROOMS")
@Data
@EqualsAndHashCode(exclude = "thematic")
@ToString(exclude = "thematic")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Comment("Clave primaria")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 10 )
    @Comment("Nombre de la habitación")
    private String name;

    @Column(name = "MAINTENANCE" )
    @Comment("Mantenimiento de la habitación")
    private String maintenance;

    @Column(name = "STATUS" )
    @Comment("Estado de la habitación")
    private Boolean status;

    @Column(name = "USER_CREATED", nullable = true, length = 100 )
    @Comment("Usuario que creó la habitación")
    private String userCreated;

    @Column(name = "DATE_CREATED", nullable = true )
    @Comment("Fecha creación de la habitación")
    private Date dateCreated;

    @Column(name = "USER_UPDATED", nullable = true, length = 100 )
    @Comment("Usuario que actualizó la habitación")
    private String userUpdated;

    @Column(name = "DATE_UPDATED", nullable = true )
    @Comment("Fecha actualización de la habitación")
    private Date dateUpdated;

    @ManyToOne
    @JoinColumn(name = "FK_THEMATIC")
    @JsonBackReference
    @Comment("Temática relacionada")
    private Thematic thematic;

}
