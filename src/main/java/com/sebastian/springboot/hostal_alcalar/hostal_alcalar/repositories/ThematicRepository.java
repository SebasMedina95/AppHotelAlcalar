package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
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

}
