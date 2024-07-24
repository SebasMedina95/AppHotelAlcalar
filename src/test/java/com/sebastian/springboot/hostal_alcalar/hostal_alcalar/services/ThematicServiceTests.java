package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data.ThematicMock;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.DetailThematicComfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateThematicDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateThematicDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.DetailThematicComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ThematicRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls.ThematicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class ThematicServiceTests {

    @Mock
    private ThematicRepository thematicRepository;

    @Mock
    private ComfortRepository comfortRepository;

    @Mock
    private DetailThematicComfortRepository detailThematicComfortRepository;


    @InjectMocks
    private ThematicServiceImpl thematicService;

    //Para los listados y obtener
    private Thematic thematic1;
    private Thematic thematic2;
    private Thematic thematic3;

    //Para la creación
    private CreateThematicDto createThematicDto;

    //Para actualización
    private ThematicMock thematicMock;

    //Listado genérico
    private List<Thematic> thematics;

    //Para proveer multiples acciones
    private static Stream<Arguments> provideThematicIds() {
        return Stream.of(
                arguments(1L, ThematicMock.thematic1),
                arguments(2L, ThematicMock.thematic2),
                arguments(3L, ThematicMock.thematic3)
        );
    }

    @BeforeEach
    void setUp() {
        // Configurar los objetos Thematic para usar en los tests
        thematic1 = ThematicMock.thematic1;
        thematic2 = ThematicMock.thematic2;
        thematic3 = ThematicMock.thematic3;

        // Configuración para la creación
        createThematicDto = ThematicMock.createThematicDto();

        // Configuración para la actualización
        thematicMock = new ThematicMock();

        // Configuración de listado
        thematics = ThematicMock.getMockThematics();
    }

    @Test
    @DisplayName("Test para obtener una temática de habitación por ID")
    void testGetThematicByIdSuccess() {

        // Configurar el comportamiento simulado del repositorio
        Long id = 1L;  // Valor específico en lugar de anyLong()
        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.of(thematic1));

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Thematic> response = thematicService.findById(id);

        // Verificar la llamada al método del repositorio
        verify(thematicRepository).getByIdComplementated(id);

        assertNotNull(response);
        assertEquals("Temática encontrada por ID correctamente", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals(thematic1.getId(), response.getData().getId());

    }

    @Test
    @DisplayName("Test para no lograr obtener una temática de habitación por ID")
    void testGetByIdNotFound() {
        // Configurar el comportamiento simulado del repositorio
        Long id = 1L;  // Valor específico en lugar de anyLong()
        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.empty());

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Thematic> response = thematicService.findById(id);

        // Verificar la llamada al método del repositorio
        verify(thematicRepository).getByIdComplementated(id);

        assertNotNull(response);
        assertEquals("La temática no pudo ser encontrado por el ID " + id, response.getErrorMessage());
        assertNull(response.getData());

    }

    @ParameterizedTest
    @MethodSource("provideThematicIds")
    @DisplayName("Test para encontrar multiples Temáticas")
    void testFindByIdMultiSuccess(Long id, Thematic expectedThematic) {
        // Configurar el comportamiento simulado del repositorio
        when(thematicRepository.getByIdComplementated(id)).thenReturn(Optional.of(expectedThematic));

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Thematic> response = thematicService.findById(id);

        // Verificar la llamada al método del repositorio
        verify(thematicRepository).getByIdComplementated(id);

        assertNotNull(response);
        assertEquals("Temática encontrada por ID correctamente", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals(expectedThematic.getId(), response.getData().getId());
        assertEquals(expectedThematic.getName(), response.getData().getName());
        assertEquals(expectedThematic.getDescription(), response.getData().getDescription());
        assertEquals(expectedThematic.getPopulate(), response.getData().getPopulate());
        assertEquals(expectedThematic.getStatus(), response.getData().getStatus());

    }

    @Test
    @DisplayName("Test para la creación exitosa de una temática")
    void testCreateThematicSuccessfullyWithComfortDetails() {

        // Definiciones
        when(thematicRepository.getThematicByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.save(any(Thematic.class))).thenAnswer(i -> i.getArgument(0));

        Comfort comfort = new Comfort(); // Crear un objeto Comfort válido
        when(comfortRepository.findById(anyLong())).thenReturn(Optional.of(comfort));

        when(detailThematicComfortRepository.save(any(DetailThematicComfort.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Llamamos el método del servicio
        ResponseWrapper<Thematic> response = thematicService.create(ThematicMock.createThematicDto());

        // Validaciones de test
        assertNotNull(response);
        assertEquals("Temática guardada correctamente", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals("TEST THEMATIC", response.getData().getName());
        assertFalse(response.getData().getDetailThematicComforts().isEmpty());

        // Verificamos los llamados
        verify(thematicRepository, times(1)).getThematicByName(anyString());
        verify(thematicRepository, times(1)).save(any(Thematic.class));
        verify(comfortRepository, times(5)).findById(anyLong());
        verify(detailThematicComfortRepository, times(5)).save(any(DetailThematicComfort.class));

    }

    @Test
    @DisplayName("Test para la creación de una temática pero el nombre ya se encuentra")
    void testCreateThematicWithDuplicateName() {

        // Definiciones
        when(thematicRepository.getThematicByName(anyString())).thenReturn(Optional.of(new Thematic()));

        // Llamado del método
        ResponseWrapper<Thematic> response = thematicService.create(createThematicDto);

        // Evaluación de respuesta
        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("El nombre de la temática ya se encuentra registrado", response.getErrorMessage());

        // Verify repository interactions
        verify(thematicRepository, times(1)).getThematicByName(anyString());
        verify(thematicRepository, never()).save(any(Thematic.class));
        verify(comfortRepository, never()).findById(anyLong());
        verify(detailThematicComfortRepository, never()).save(any(DetailThematicComfort.class));

    }

    @Test
    @DisplayName("Test para la creación exitosa de una temática pero se hallaron detalles en las comodidades")
    void testCreateThematicWithMissingComforts() {
        // Mock the repository behavior
        when(thematicRepository.getThematicByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.save(any(Thematic.class))).thenAnswer(i -> i.getArgument(0));

        Comfort comfort1 = new Comfort(); // Crear objetos Comfort válidos
        Comfort comfort2 = new Comfort();

        // Configuración del mock para manejar comodidades existentes y faltantes
        when(comfortRepository.findById(1L)).thenReturn(Optional.of(comfort1));
        when(comfortRepository.findById(2L)).thenReturn(Optional.of(comfort2));
        when(comfortRepository.findById(3L)).thenReturn(Optional.empty());
        when(comfortRepository.findById(4L)).thenReturn(Optional.empty());
        when(comfortRepository.findById(5L)).thenReturn(Optional.empty());

        when(detailThematicComfortRepository.save(any(DetailThematicComfort.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Llamamos el método del servicio
        ResponseWrapper<Thematic> response = thematicService.create(ThematicMock.createThematicDto());

        // Validaciones de test
        assertNotNull(response);
        assertEquals("Temática guardada correctamente, existen comodidades que no pudieron ser registradas, revise el log", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals("TEST THEMATIC", response.getData().getName());
        assertFalse(response.getData().getDetailThematicComforts().isEmpty());

        // Verificamos los llamados
        verify(thematicRepository, times(1)).getThematicByName(anyString());
        verify(thematicRepository, times(1)).save(any(Thematic.class));
        verify(comfortRepository, times(5)).findById(anyLong());
        verify(detailThematicComfortRepository, times(2)).save(any(DetailThematicComfort.class));

    }

    @Test
    @DisplayName("Test para creación de una temática pero obtenemos una excepción")
    void testCreateThematicWithException() {

        when(thematicRepository.getThematicByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.save(any(Thematic.class))).thenThrow(new RuntimeException("Test Exception"));

        ResponseWrapper<Thematic> response = thematicService.create(createThematicDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("La temática no pudo ser creada", response.getErrorMessage());

        verify(thematicRepository, times(1)).getThematicByName(anyString());
        verify(thematicRepository, times(1)).save(any(Thematic.class));
        verify(comfortRepository, never()).findById(anyLong());
        verify(detailThematicComfortRepository, never()).save(any(DetailThematicComfort.class));

    }

    @Test
    @DisplayName("Test para listar todas las temáticas con paginación, filtro y ajuste de relaciones")
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Thematic> page = new PageImpl<>(thematics, pageable, thematics.size());

        when(thematicRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Thematic> result = thematicService.findAll("searchTerm", pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(thematics.get(0), result.getContent().get(0));
        assertEquals(thematics.get(1), result.getContent().get(1));
        verify(thematicRepository, times(1)).findAll(any(Specification.class), eq(pageable));

    }

    @Test
    @DisplayName("Test para actualización exitosa de una temática")
    void testUpdateThematicSuccessfully() {

        UpdateThematicDto updateThematicDto = thematicMock.getUpdateThematicDto();
        Thematic thematic = thematicMock.getThematic();
        Thematic savedThematic = thematicMock.getThematic();

        when(thematicRepository.getByIdComplementated(anyLong()))
                .thenReturn(Optional.of(thematic))  // Primera llamada
                .thenReturn(Optional.of(savedThematic));  // Segunda llamada
        when(thematicRepository.getThematicByNameForEdit(anyString(), anyLong())).thenReturn(Optional.empty());
        when(thematicRepository.save(any(Thematic.class))).thenReturn(savedThematic);
        when(comfortRepository.findById(anyLong())).thenReturn(Optional.of(thematicMock.getComfort()));
        when(detailThematicComfortRepository.save(any(DetailThematicComfort.class))).thenReturn(new DetailThematicComfort());

        ResponseWrapper<Object> response = thematicService.update(1L, updateThematicDto);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("Temática Actualizada Correctamente", response.getErrorMessage());

        verify(thematicRepository, times(2)).getByIdComplementated(anyLong());
        verify(thematicRepository, times(1)).getThematicByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, times(1)).save(any(Thematic.class));
        verify(comfortRepository, times(updateThematicDto.getComfortsListId().size())).findById(anyLong());
        verify(detailThematicComfortRepository, times(updateThematicDto.getComfortsListId().size())).save(any(DetailThematicComfort.class));

    }

    @Test
    @DisplayName("Test para actualización de una temática pero se repite el nombre")
    void testUpdateThematicNameExists() {

        UpdateThematicDto updateThematicDto = thematicMock.getUpdateThematicDto();
        Thematic thematic = thematicMock.getThematic();

        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.of(thematic));
        when(thematicRepository.getThematicByNameForEdit(anyString(), anyLong())).thenReturn(Optional.of(thematicMock.getThematic()));

        ResponseWrapper<Object> response = thematicService.update(1L, updateThematicDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("El nombre de la temática ya está registrado", response.getErrorMessage());

        verify(thematicRepository, times(1)).getByIdComplementated(anyLong());
        verify(thematicRepository, times(1)).getThematicByNameForEdit(anyString(), anyLong());

    }

    @Test
    @DisplayName("Test para actualización de una temática pero no fue encontrada la temática")
    void testUpdateThematicNotFound() {

        UpdateThematicDto updateThematicDto = thematicMock.getUpdateThematicDto();

        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Object> response = thematicService.update(1L, updateThematicDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("La temática no fue encontrada", response.getErrorMessage());

        verify(thematicRepository, times(1)).getByIdComplementated(anyLong());

    }

    @Test
    @DisplayName("Test para actualización exitosa de una temática pero no se encuentra alguna comodidad")
    void testUpdateThematicComfortNotFound() {

        UpdateThematicDto updateThematicDto = thematicMock.getUpdateThematicDto();
        Thematic thematic = thematicMock.getThematic();

        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.of(thematic));
        when(thematicRepository.getThematicByNameForEdit(anyString(), anyLong())).thenReturn(Optional.empty());
        when(thematicRepository.save(any(Thematic.class))).thenReturn(thematic);
        when(comfortRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseWrapper<Object> response = thematicService.update(1L, updateThematicDto);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("Temática actualizada correctamente, existen comodidades que no pudieron ser registradas, revise el log", response.getErrorMessage());

        verify(thematicRepository, times(1)).getByIdComplementated(anyLong());
        verify(thematicRepository, times(1)).getThematicByNameForEdit(anyString(), anyLong());
        verify(thematicRepository, times(1)).save(any(Thematic.class));
        verify(comfortRepository, times(updateThematicDto.getComfortsListId().size())).findById(anyLong());

    }

    @Test
    @DisplayName("Test para actualización pero obtuvimos alguna excepción")
    void testUpdateThematicThrowsException() {

        UpdateThematicDto updateThematicDto = thematicMock.getUpdateThematicDto();

        when(thematicRepository.getByIdComplementated(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseWrapper<Object> response = thematicService.update(1L, updateThematicDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("La temática no pudo ser actualizada", response.getErrorMessage());

        verify(thematicRepository, times(1)).getByIdComplementated(anyLong());

    }

    @Test
    @DisplayName("Test eliminar temática exitosamente")
    void testDeleteThematicSuccessfully() {

        when(thematicRepository.getByIdComplementated(1L)).thenReturn(Optional.of(thematic2));
        when(thematicRepository.save(any(Thematic.class))).thenReturn(thematic2);

        ResponseWrapper<Object> response = thematicService.delete(1L);

        assertNotNull(response);
        assertEquals("Temática Eliminada Correctamente", response.getErrorMessage());
        assertNotNull(response.getData());

        verify(thematicRepository, times(1)).getByIdComplementated(1L);
        verify(thematicRepository, times(1)).save(thematic2);

    }

    @Test
    @DisplayName("Test eliminar temática pero no fue encontrada")
    void testDeleteThematicNotFound() {

        when(thematicRepository.getByIdComplementated(1L)).thenReturn(Optional.empty());

        ResponseWrapper<Object> response = thematicService.delete(1L);

        assertNotNull(response);
        assertEquals("La temática no fue encontrado", response.getErrorMessage());
        assertNull(response.getData());

        verify(thematicRepository, times(1)).getByIdComplementated(1L);
        verify(thematicRepository, never()).save(any(Thematic.class));

    }

    @Test
    @DisplayName("Test eliminar temática con excepción")
    void testDeleteThematicWithException() {

        when(thematicRepository.getByIdComplementated(1L)).thenThrow(new RuntimeException("Test Exception"));

        ResponseWrapper<Object> response = thematicService.delete(1L);

        assertNotNull(response);
        assertEquals("La temática no pudo ser eliminada", response.getErrorMessage());
        assertNull(response.getData());

        verify(thematicRepository, times(1)).getByIdComplementated(1L);
        verify(thematicRepository, never()).save(any(Thematic.class));

    }

}
