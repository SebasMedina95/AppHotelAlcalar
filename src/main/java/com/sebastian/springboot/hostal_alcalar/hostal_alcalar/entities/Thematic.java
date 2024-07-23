package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "TBL_THEMATICS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@EqualsAndHashCode(exclude = {"rooms", "detailThematicComforts"})
@ToString(exclude = {"rooms", "detailThematicComforts"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Para la carga dinámica y aplicar las relaciones en paginación y filtro embebido.
// Le damos el nombre de Thematic.detail y asociamos las entidades de relación por los campos referenciales,
// si dado el caso un campo referencial requerimos referencial un elemento de este particular, aplicamos un subgraph.
//? ESTO ES USADO PARA CUANDO APLICAMOS findAll() o findOne() de JPA/Hibernate, solo para estos casos
@NamedEntityGraph(
        name = "Thematic.detail",
        attributeNodes = {
                @NamedAttributeNode("rooms"),
                @NamedAttributeNode("detailThematicComforts"),
                @NamedAttributeNode(value = "detailThematicComforts", subgraph = "detailThematicComforts-comfort")
        },
        subgraphs = {
                @NamedSubgraph(name = "detailThematicComforts-comfort", attributeNodes = @NamedAttributeNode("comfort"))
        }
)
public class Thematic {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Comment("Clave primaria")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 150 )
    @Comment("Nombre de la temática")
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 1000 )
    @Comment("Descripción de la temática")
    private String description;

    @Column(name = "POPULATE", nullable = false )
    @Comment("Popularidad en Cantidad de la temática")
    private Integer populate;

    @Column(name = "STATUS" )
    @Comment("Estado de la temática")
    private Boolean status;

    @Column(name = "USER_CREATED", nullable = true, length = 100 )
    @Comment("Usuario que creó la temática")
    private String userCreated;

    @Column(name = "DATE_CREATED", nullable = true )
    @Comment("Fecha creación de la temática")
    private Date dateCreated;

    @Column(name = "USER_UPDATED", nullable = true, length = 100 )
    @Comment("Usuario que actualizó la temática")
    private String userUpdated;

    @Column(name = "DATE_UPDATED", nullable = true )
    @Comment("Fecha actualización de la temática")
    private Date dateUpdated;

    @OneToMany(mappedBy = "thematic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Room> rooms;

    @OneToMany(mappedBy = "thematic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<DetailThematicComfort> detailThematicComforts;

}
