package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    @Query("SELECT r FROM Room r WHERE UPPER(r.name) = UPPER(:roomName)")
    Optional<Room> getRoomByName(String roomName);

    @Query("SELECT r FROM Room r WHERE UPPER(r.name) = UPPER(:roomName) AND r.id <> :id")
    Optional<Room> getRoomByNameForEdit(String roomName, Long id);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.thematic WHERE r.status = true AND LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Room> findAllWithThematic(@Param("search") String search, Pageable pageable);

}
