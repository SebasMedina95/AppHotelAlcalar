package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ThematicRepository extends JpaRepository<Thematic, Long>, JpaSpecificationExecutor<Thematic> {

    @Query("SELECT t FROM Thematic t " +
            "LEFT JOIN FETCH t.rooms " +
            "LEFT JOIN FETCH t.detailThematicComforts dtc " +
            "LEFT JOIN FETCH dtc.comfort " +
            "WHERE t.id = :id")
    Optional<Thematic> getByIdComplementated(@Param("id") Long id);

    // Implementación especial: Uso EntityGraph para cargar las entidades relacionadas y aplicar en embebido
    // los filtros así como paginación, mi entidad Thematic debe tener definida el NamedEntityGraph y ajusto
    // las entidades de relación, debemos tener muy en cuenta las mejores prácticas cuando es mucha data.
    // El nombre que le dimos desde la Entidad de Thematic es Thematic.detail
    @EntityGraph(value = "Thematic.detail", type = EntityGraph.EntityGraphType.LOAD)
    Page<Thematic> findAll(Specification<Thematic> spec, Pageable pageable);

    @Query("SELECT t FROM Thematic t WHERE UPPER(t.name) = UPPER(:thematicName)")
    Optional<Thematic> getThematicByName(String thematicName);

}
