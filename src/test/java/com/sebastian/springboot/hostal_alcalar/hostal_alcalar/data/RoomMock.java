package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateRoomDto;

import java.util.Date;

public class RoomMock {

    public static Thematic createThematic() {
        return Thematic.builder()
                .id(1L) // Asegúrate de que este ID coincida con el que usas en createRoomDto
                .name("Test Thematic")
                .build();
    }

    public static CreateRoomDto createRoomDto() {
        return CreateRoomDto.builder()
                .name("Test Room New")
                .maintenance("No")
                .thematicId(1L) // Asegúrate de que este ID coincida con el ID de Thematic
                .build();
    }

    public static Room createRoom() {
        return Room.builder()
                .id(1L)
                .name("Test Room Old DB")
                .maintenance("No")
                .status(true)
                .userCreated("testUser")
                .dateCreated(new Date())
                .userUpdated("testUser")
                .dateUpdated(new Date())
                .thematic(createThematic())
                .build();
    }

}
