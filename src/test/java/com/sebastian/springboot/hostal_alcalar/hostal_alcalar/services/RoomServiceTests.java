package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data.RoomMock;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.RoomRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ThematicRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls.RoomServiceImpl;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls.ThematicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class RoomServiceTests {

    @Mock
    private ThematicRepository thematicRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Test para la creación exitosa de una habitación")
    void testCreateRoomSuccessfully() {
        Thematic thematic = RoomMock.createThematic();
        CreateRoomDto createRoomDto = RoomMock.createRoomDto();
        Room room = RoomMock.createRoom();

        // Configurando los mocks correctamente
        when(roomRepository.getRoomByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.findById(createRoomDto.getThematicId())).thenReturn(Optional.of(thematic));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        // Llamando al servicio
        ResponseWrapper<Room> response = roomService.create(createRoomDto);

        // Validando los resultados
        assertNotNull(response.getData(), "El registro no puede ser null");
        assertEquals("Habitación guardada correctamente", response.getErrorMessage());
        assertEquals(room, response.getData(), "La habitación guardada debe ser la misma a la devuelta");
    }

    @Test
    @DisplayName("Test para la creación de una habitación pero su nombre ya existe")
    void testCreateRoomNameAlreadyExists() {
        CreateRoomDto createRoomDto = RoomMock.createRoomDto();
        Room room = RoomMock.createRoom();

        when(roomRepository.getRoomByName(anyString())).thenReturn(Optional.of(room));

        ResponseWrapper<Room> response = roomService.create(createRoomDto);

        assertNull(response.getData());
        assertEquals("El nombre de la habitación ya se encuentra registrado", response.getErrorMessage());
    }

    @Test
    @DisplayName("Test para la creación de una habitación pero la temática no es válida")
    void testCreateRoomThematicDoesNotExist() {
        CreateRoomDto createRoomDto = RoomMock.createRoomDto();

        when(roomRepository.getRoomByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Room> response = roomService.create(createRoomDto);

        assertNull(response.getData());
        assertEquals("La temática a la que está intentando asociar la habitación no existe", response.getErrorMessage());
    }

    @Test
    @DisplayName("Test para la creación de una habitación pero lanzo una excepción")
    void testCreateRoomException() {
        CreateRoomDto createRoomDto = RoomMock.createRoomDto();

        when(roomRepository.getRoomByName(anyString())).thenThrow(new RuntimeException("Database Error"));

        ResponseWrapper<Room> response = roomService.create(createRoomDto);

        assertNull(response.getData());
        assertEquals("La habitación no pudo ser creada", response.getErrorMessage());
    }

}
