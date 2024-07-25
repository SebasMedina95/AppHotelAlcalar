package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.others.GenericRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    ResponseWrapper<Room> create(CreateRoomDto room);
    Page<GenericRoomDto> findAll(String search, Pageable pageable);
    ResponseWrapper<Room> findById(Long id);
    ResponseWrapper<Room> update(Long id, UpdateRoomDto room);
    ResponseWrapper<Room> delete(Long id);

}
