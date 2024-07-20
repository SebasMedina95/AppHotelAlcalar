package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data.ThematicMock;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
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

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class ThematicServiceTests {

    @Mock
    private ThematicRepository thematicRepository;

    @InjectMocks
    private ThematicServiceImpl thematicService;

    private Thematic thematic1;
    private Thematic thematic2;
    private Thematic thematic3;

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
    }

    @Test
    @DisplayName("Test para obtener una temática de habitación por ID")
    void testGetThematicByIdSuccess() {

        // Configurar el comportamiento simulado del repositorio
        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.of(thematic1));

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Thematic> response = thematicService.findById(anyLong());

        assertNotNull(response);
        assertEquals("Temática encontrada por ID correctamente", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals(thematic1.getId(), response.getData().getId());

    }

    @Test
    @DisplayName("Test para no lograr obtener una temática de habitación por ID")
    void testGetByIdNotFound() {

        // Configurar el comportamiento simulado del repositorio
        when(thematicRepository.getByIdComplementated(anyLong())).thenReturn(Optional.empty());

        // Llamar al método de servicio y verificar el resultado
        ResponseWrapper<Thematic> response = thematicService.findById(anyLong());

        assertNotNull(response);
        assertEquals("La temática no pudo ser encontrado por el ID", response.getErrorMessage());
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

        assertNotNull(response);
        assertEquals("Temática encontrada por ID correctamente", response.getErrorMessage());
        assertNotNull(response.getData());
        assertEquals(expectedThematic.getId(), response.getData().getId());
        assertEquals(expectedThematic.getName(), response.getData().getName());
        assertEquals(expectedThematic.getDescription(), response.getData().getDescription());
        assertEquals(expectedThematic.getPopulate(), response.getData().getPopulate());
        assertEquals(expectedThematic.getStatus(), response.getData().getStatus());
    }


}
