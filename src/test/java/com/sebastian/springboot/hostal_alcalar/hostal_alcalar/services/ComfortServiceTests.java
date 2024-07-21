package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.dtos.PaginationDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data.ComfortMock;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls.ComfortServiceImpl;
import jakarta.validation.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ComfortServiceTests {

    @Mock
    private ComfortRepository comfortRepository;

    @InjectMocks
    private ComfortServiceImpl comfortService;

    private CreateComfortDto comfortDto;
    private UpdateComfortDto comfortDtoUpdt;
    private Comfort comfort;
    private List<Comfort> comfortList;
    private Validator validator;
    private Comfort dataComfort1;
    private Comfort dataComfort2;
    private Comfort dataComfort3;

    @BeforeEach
    public void setUp() {
        comfortDto = ComfortMock.createComfortDto();
        comfortDtoUpdt = ComfortMock.updateComfortDto();
        comfort = ComfortMock.createComfort();
        comfortList = ComfortMock.comfortList();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        dataComfort1 = ComfortMock.comfort1;
        dataComfort2 = ComfortMock.comfort2;
        dataComfort3 = ComfortMock.comfort3;

    }

    @Test
    @DisplayName("Test para registrar una Comodidad pero su nombre ya está")
    void testCreateComfortNameAlreadyExists() {

        // Configuración del mock para devolver un Optional vacío
        when(comfortRepository.getComfortByName("TEST COMFORT")).thenReturn(Optional.of(comfort));

        ResponseWrapper<Comfort> response = comfortService.create(comfortDto);

        assertNull(response.getData());
        assertEquals("El nombre de la comodidad ya se encuentra registrado", response.getErrorMessage());
    }

    @Test
    @DisplayName("Test para registrar una Comodidad y el Registro es Exitoso")
    void testCreateComfortSuccessfully() {

        // Configuración del mock para devolver un Optional vacío
        when(comfortRepository.getComfortByName("TEST COMFORT")).thenReturn(Optional.empty());
        // Configuración del mock para devolver el objeto Comfort cuando se guarda
        when(comfortRepository.save(any(Comfort.class))).thenReturn(comfort);

        ResponseWrapper<Comfort> response = comfortService.create(comfortDto);

        assertNotNull(response.getData());
        assertEquals("Comfort guardado correctamente", response.getErrorMessage());
    }

    @Test
    @DisplayName("Test para registrar una Comodidad pero se generó una Excepción")
    void testCreateComfortException() {
        when(comfortRepository.getComfortByName("TEST COMFORT")).thenThrow(new RuntimeException("Database error"));

        ResponseWrapper<Comfort> response = comfortService.create(comfortDto);

        assertNull(response.getData());
        assertEquals("La comodidad no pudo ser creada", response.getErrorMessage());
    }

    @Test
    @DisplayName("Test para obtener todas las comodidades paginas y filtradas")
    void testFindAllWithPaginationAndFiltering() {
        String search = "Comfort";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comfort> page = new PageImpl<>(comfortList, pageable, comfortList.size());

        when(comfortRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Comfort> result = comfortService.findAll(search, pageable);

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Comfort1");
    }

    @Test
    @DisplayName("Test para validar las propiedades requeridas para la paginación de mostrar todas las comodidades")
    void testPaginationDtoValidation() {
        validatePaginationDto(PaginationDto.builder()
                .page(null)
                .size(10)
                .order("asc")
                .sort("name")
                .build(), "El número de página actual es requerido");

        validatePaginationDto(PaginationDto.builder()
                .page(1)
                .size(null)
                .order("asc")
                .sort("name")
                .build(), "El tamaño de página es requerido");

        validatePaginationDto(PaginationDto.builder()
                .page(1)
                .size(10)
                .order(null)
                .sort("name")
                .build(), "El valor de ordenamiento de página actual es requerido");

        validatePaginationDto(PaginationDto.builder()
                .page(1)
                .size(10)
                .order("asc")
                .sort(null)
                .build(), "La columna para el ordenamiento de página actual es requerido");
    }

    //! Método privado para validar en conjunto la paginación dado diversos campos en una sola
    private void validatePaginationDto(PaginationDto paginationDto, String expectedMessage) {
        Set<ConstraintViolation<PaginationDto>> violations = validator.validate(paginationDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Test para validar la búsqueda de elementos paginados al mostrar todas las comodidades (No devuelve nada)")
    void testFindAllWithEmptyResult() {
        String search = "NonExistentComfort";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comfort> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(comfortRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Comfort> result = comfortService.findAll(search, pageable);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Test para validar la búsqueda de elementos paginados al mostrar todas las comodidades (Devuelve resultados)")
    void testFindAllWithSearchResult() {

        // Configuración del filtro de búsqueda
        String search = "Comfort2";
        Pageable pageable = PageRequest.of(0, 10);

        // Filtrar los resultados manualmente para simular la búsqueda
        List<Comfort> filteredList = comfortList.stream()
                .filter(c -> c.getName().equals(search))
                .toList();

        Page<Comfort> page = new PageImpl<>(filteredList, pageable, filteredList.size());

        // Configura el mock para devolver la página filtrada
        when(comfortRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // Llama al servicio
        Page<Comfort> result = comfortService.findAll(search, pageable);

        // Verifica los resultados
        assertThat(result.getTotalElements()).isEqualTo(filteredList.size());
        assertThat(result.getContent()).containsExactlyElementsOf(filteredList);
    }

    @Test
    @DisplayName("Test para obtener una comodidad por ID")
    void testGetThematicByIdSuccess() {

        // Configurar el comportamiento simulado del repositorio
        Long id = 1L;  // Valor específico en lugar de anyLong()
        when(comfortRepository.findById(anyLong())).thenReturn(Optional.of(dataComfort1));

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Comfort> response = comfortService.findById(id);

        // Verificar la llamada al método del repositorio
        verify(comfortRepository).findById(id);

        assertNotNull(response);
        assertEquals("Comodidad encontrada por ID correctamente", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals(dataComfort1.getId(), response.getData().getId());

    }

    @Test
    @DisplayName("Test para no lograr obtener una comodidad de habitación por ID")
    void testGetByIdNotFound() {

        // Configurar el comportamiento simulado del repositorio
        Long id = 1L;  // Valor específico en lugar de anyLong()
        when(comfortRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Comfort> response = comfortService.findById(id);

        // Verificar la llamada al método del repositorio
        verify(comfortRepository).findById(id);

        assertNotNull(response);
        assertEquals("La comodidad no pudo ser encontrado por el ID " + id, response.getErrorMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Test para actualizar una comodidad pero no se encontró por ID")
    void testUpdateComfortNotFound() {

        when(comfortRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Comfort> response = comfortService.update(1L, comfortDtoUpdt);

        assertNull(response.getData());
        assertEquals("La comodidad no fue encontrada", response.getErrorMessage());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, never()).save(any(Comfort.class));

    }

    @Test
    @DisplayName("Test para actualizar una comodidad pero el nombre de comodidad ya existe")
    void testUpdate_ComfortNameAlreadyExists() {

        when(comfortRepository.findById(anyLong())).thenReturn(Optional.of(comfort));
        when(comfortRepository.getComfortByNameForEdit(anyString(), anyLong())).thenReturn(Optional.of(new Comfort()));

        ResponseWrapper<Comfort> response = comfortService.update(1L, comfortDtoUpdt);

        assertNull(response.getData());
        assertEquals("El nombre de la comodidad ya está registrado", response.getErrorMessage());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, times(1)).getComfortByNameForEdit(anyString(), anyLong());
        verify(comfortRepository, never()).save(any(Comfort.class));

    }

    @Test
    @DisplayName("Test para actualizar una comodidad y la actualización es exitosa")
    void testUpdate_SuccessfulUpdate() {

        when(comfortRepository.findById(anyLong())).thenReturn(Optional.of(comfort));
        when(comfortRepository.getComfortByNameForEdit(anyString(), anyLong())).thenReturn(Optional.empty());
        when(comfortRepository.save(any(Comfort.class))).thenReturn(comfort);

        ResponseWrapper<Comfort> response = comfortService.update(1L, comfortDtoUpdt);

        assertNotNull(response.getData());
        assertEquals("Comodidad Actualizada Correctamente", response.getErrorMessage());
        assertEquals("Test Comfort Update".toUpperCase(), response.getData().getName());
        assertEquals("<i class='fa-solid fa-trash'></i>", response.getData().getIcon());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, times(1)).getComfortByNameForEdit(anyString(), anyLong());
        verify(comfortRepository, times(1)).save(any(Comfort.class));

    }

    @Test
    @DisplayName("Test para actualizar una comodidad y ocurre un error Excepción")
    void testUpdate_ExceptionThrown() {
        when(comfortRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseWrapper<Comfort> response = comfortService.update(1L, comfortDtoUpdt);

        assertNull(response.getData());
        assertEquals("La comodidad no pudo ser actualizada", response.getErrorMessage());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, never()).save(any(Comfort.class));
    }

    @Test
    @DisplayName("Test para eliminar lógicamente una comodidad pero no se encuentra")
    void testDeleteComfortNotFound() {

        when(comfortRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Comfort> response = comfortService.delete(1L);

        assertNull(response.getData());
        assertEquals("La comodidad no fue encontrado", response.getErrorMessage());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, never()).save(any(Comfort.class));

    }

    @Test
    @DisplayName("Test para eliminar lógicamente una comodidad y es encontrada")
    void testDeleteSuccessfulLogicalDelete() {

        when(comfortRepository.findById(anyLong())).thenReturn(Optional.of(comfort));
        when(comfortRepository.save(any(Comfort.class))).thenReturn(comfort);

        ResponseWrapper<Comfort> response = comfortService.delete(1L);

        assertNotNull(response.getData());
        assertEquals("Comodidad Eliminada Correctamente", response.getErrorMessage());
        assertFalse(response.getData().getStatus());
        assertEquals("usuario123", response.getData().getUserUpdated());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, times(1)).save(any(Comfort.class));

    }

    @Test
    @DisplayName("Test para eliminar lógicamente una comodidad y ocurre un error Excepción")
    void testDeleteExceptionThrown() {

        when(comfortRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseWrapper<Comfort> response = comfortService.delete(1L);

        assertNull(response.getData());
        assertEquals("La comodidad no pudo ser eliminada", response.getErrorMessage());
        verify(comfortRepository, times(1)).findById(anyLong());
        verify(comfortRepository, never()).save(any(Comfort.class));

    }

}
