package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data.ThematicMock;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.DetailThematicComfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateThematicDto;
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
        verify(comfortRepository, times(5)).findById(anyLong()); // Debería ser llamado 5 veces
        verify(detailThematicComfortRepository, times(2)).save(any(DetailThematicComfort.class)); // Debería ser llamado 2 veces
    }

    @Test
    @DisplayName("Test para creación de una temática pero obtenemos una excepción")
    void testCreateThematicWithException() {
        // Mock the repository behavior
        when(thematicRepository.getThematicByName(anyString())).thenReturn(Optional.empty());
        when(thematicRepository.save(any(Thematic.class))).thenThrow(new RuntimeException("Test Exception"));

        // Call the service method
        ResponseWrapper<Thematic> response = thematicService.create(createThematicDto);

        // Assert the response
        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("La temática no pudo ser creada", response.getErrorMessage());

        // Verify repository interactions
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


}
