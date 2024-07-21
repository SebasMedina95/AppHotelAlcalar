package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ComfortRepository extends JpaRepository<Comfort, Long>, JpaSpecificationExecutor<Comfort> {

    @Query("SELECT c FROM Comfort c WHERE UPPER(c.name) = UPPER(:comfortName)")
    Optional<Comfort> getComfortByName(String comfortName);

}
