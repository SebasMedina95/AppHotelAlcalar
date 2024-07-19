package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Entity
@Table(name = "TBL_COMFORTS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comfort {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Comment("Clave primaria")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 200 )
    @Comment("Nombre de la comodidad")
    private String name;

    @Column(name = "STATUS" )
    @Comment("Estado de la comodidad")
    private Boolean status;

    @Column(name = "ICON", nullable = false, length = 200 )
    @Comment("Link HTML de ícono para la comodidad")
    private String icon;

    @Column(name = "USER_CREATED", nullable = true, length = 100 )
    @Comment("Usuario que creó la comodidad")
    private String userCreated;

    @Column(name = "DATE_CREATED", nullable = true )
    @Comment("Fecha creación de la comodidad")
    private Date dateCreated;

    @Column(name = "USER_UPDATED", nullable = true, length = 100 )
    @Comment("Usuario que actualizó la comodidad")
    private String userUpdated;

    @Column(name = "DATE_UPDATED", nullable = true )
    @Comment("Fecha actualización de la comodidad")
    private Date dateUpdated;

}
