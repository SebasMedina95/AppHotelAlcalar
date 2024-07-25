package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data.RoomMock;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.others.GenericRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.RoomRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ThematicRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTests {

    @Mock
    private ThematicRepository thematicRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;
    private Thematic thematic;
    private Pageable pageable;
    private UpdateRoomDto updateRoomDto;

    @BeforeEach
    void setUp() {
        room = RoomMock.getRoom();
        thematic = RoomMock.getThematic();
        pageable = PageRequest.of(0, 10);
        updateRoomDto = RoomMock.getUpdateRoomDto();
    }

    @Test
    @DisplayName("Test para la creación exitosa de una habitación")
    void testCreateRoomSuccessfully() {
        Thematic thematicRoot = RoomMock.createThematic();
        CreateRoomDto createRoomDto = RoomMock.createRoomDto();
        Room roomThematic = RoomMock.createRoom();

        // Configurando los mocks correctamente
        when(roomRepository.getRoomByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.findById(createRoomDto.getThematicId())).thenReturn(Optional.of(thematicRoot));
        when(roomRepository.save(any(Room.class))).thenReturn(roomThematic);

        // Llamando al servicio
        ResponseWrapper<Room> response = roomService.create(createRoomDto);

        // Validando los resultados
        assertNotNull(response.getData(), "El registro no puede ser null");
        assertEquals("Habitación guardada correctamente", response.getErrorMessage());
        assertEquals(roomThematic, response.getData(), "La habitación guardada debe ser la misma a la devuelta");
    }

    @Test
    @DisplayName("Test para la creación de una habitación pero su nombre ya existe")
    void testCreateRoomNameAlreadyExists() {
        CreateRoomDto createRoomDto = RoomMock.createRoomDto();
        Room roomExist = RoomMock.createRoom();

        when(roomRepository.getRoomByName(anyString())).thenReturn(Optional.of(roomExist));

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

    @Test
    @DisplayName("Test para listar todas las habitaciones")
    void testFindAll() {
        // Configura los mocks
        when(roomRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(room), pageable, 1));
        when(thematicRepository.findById(any(Long.class))).thenReturn(Optional.of(thematic));

        // Ejecuta el método a probar
        Page<GenericRoomDto> result = roomService.findAll("test", pageable);

        // Verifica resultados
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Room", result.getContent().get(0).getName());
        assertEquals("Test Thematic", result.getContent().get(0).getThematicName());
    }

    @Test
    @DisplayName("Test para listar todas las habitaciones con búsqueda pero no hallamos información")
    void testFindAllWithNoSearch() {
        when(roomRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(room), pageable, 1));
        when(thematicRepository.findById(any(Long.class))).thenReturn(Optional.of(thematic));

        Page<GenericRoomDto> result = roomService.findAll("", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Room", result.getContent().get(0).getName());
        assertEquals("Test Thematic", result.getContent().get(0).getThematicName());
    }

    @Test
    @DisplayName("Test para listar todas las habitaciones pero no se halló la temática")
    void testFindAllWithNoThematic() {
        room.setThematic(null);
        when(roomRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(room), pageable, 1));

        Page<GenericRoomDto> result = roomService.findAll("test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Room", result.getContent().get(0).getName());
        assertNull(result.getContent().get(0).getThematicName());
    }

    @Test
    @DisplayName("Test para actualizar una habitación")
    void testUpdate() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomRepository.getRoomByNameForEdit(anyString(), anyLong())).thenReturn(Optional.empty());
        when(thematicRepository.findById(anyLong())).thenReturn(Optional.of(thematic));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        ResponseWrapper<Room> result = roomService.update(1L, updateRoomDto);

        assertNotNull(result);
        assertEquals("Habitación Actualizada Correctamente", result.getErrorMessage());
        assertNotNull(result.getData());
        assertEquals("UPDATED ROOM", result.getData().getName());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).getRoomByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, times(1)).findById(anyLong());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("Test cuando la habitación no es encontrada")
    void testUpdateRoomNotFound() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Room> result = roomService.update(1L, updateRoomDto);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La habitación no fue encontrada", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, never()).getRoomByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, never()).findById(anyLong());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Test cuando el nombre de la habitación ya está registrado")
    void testUpdateRoomNameAlreadyExists() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomRepository.getRoomByNameForEdit(anyString(), anyLong())).thenReturn(Optional.of(room));

        ResponseWrapper<Room> result = roomService.update(1L, updateRoomDto);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("El nombre de la habitación ya está registrado", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).getRoomByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, never()).findById(anyLong());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Test cuando la temática no es encontrada")
    void testUpdateThematicNotFound() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomRepository.getRoomByNameForEdit(anyString(), anyLong())).thenReturn(Optional.empty());
        when(thematicRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Room> result = roomService.update(1L, updateRoomDto);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La temática a la que está intentando asociar la habitación no existe", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).getRoomByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, times(1)).findById(anyLong());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Test cuando ocurre un error al actualizar")
    void testUpdateException() {
        when(roomRepository.findById(anyLong())).thenThrow(new RuntimeException("Test Exception"));

        ResponseWrapper<Room> result = roomService.update(1L, updateRoomDto);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La temática no pudo ser actualizada", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, never()).getRoomByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, never()).findById(anyLong());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Test para eliminar una habitación exitosamente")
    void testDelete() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        ResponseWrapper<Room> result = roomService.delete(1L);

        assertNotNull(result);
        assertEquals("Habitación Eliminada Correctamente", result.getErrorMessage());
        assertNotNull(result.getData());
        assertFalse(result.getData().getStatus());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("Test cuando la habitación no es encontrada")
    void testDeleteRoomNotFound() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Room> result = roomService.delete(1L);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La habitación no fue encontrado", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Test cuando ocurre un error al eliminar")
    void testDeleteException() {
        when(roomRepository.findById(anyLong())).thenThrow(new RuntimeException("Test Exception"));

        ResponseWrapper<Room> result = roomService.delete(1L);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La habitación no pudo ser eliminada", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Test para obtener una habitación por ID exitosamente")
    void testFindById() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));

        ResponseWrapper<GenericRoomDto> result = roomService.findById(1L);

        assertNotNull(result);
        assertEquals("Habitación encontrada por ID correctamente", result.getErrorMessage());
        assertNotNull(result.getData());
        assertEquals("Test Room", result.getData().getName());
        assertEquals("Test Thematic", result.getData().getThematicName());

        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test cuando la habitación no es encontrada por ID")
    void testFindByIdRoomNotFound() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<GenericRoomDto> result = roomService.findById(1L);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La habitación no pudo ser encontrado por el ID 1", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test cuando ocurre un error al obtener la habitación por ID")
    void testFindByIdException() {
        when(roomRepository.findById(anyLong())).thenThrow(new RuntimeException("Test Exception"));

        ResponseWrapper<GenericRoomDto> result = roomService.findById(1L);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("La habitación no pudo ser encontrado por el ID", result.getErrorMessage());

        verify(roomRepository, times(1)).findById(1L);
    }


}
