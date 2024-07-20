package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.controllers;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ApiResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.CustomPagedResourcesAssembler;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ThematicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/thematics")
@Tag(name = "Controlador de Temáticas", description = "Operaciones relacionadas con las temáticas")
public class ThematicController {

    private final ThematicService thematicService;
    private final CustomPagedResourcesAssembler<Thematic> customPagedResourcesAssembler;

    @Autowired
    public ThematicController(
            ThematicService thematicService,
            CustomPagedResourcesAssembler<Thematic> customPagedResourcesAssembler){
        this.thematicService = thematicService;
        this.customPagedResourcesAssembler = customPagedResourcesAssembler;
    }

    @GetMapping("/find-by-id/{id}")
    @Operation(summary = "Obtener temáticas por ID", description = "Obtener una temática dado el ID")
    public ResponseEntity<ApiResponse<Thematic>> findById(@PathVariable String id){

        ResponseWrapper<Thematic> thematic;

        try {
            Long thematicId = Long.parseLong(id);
            thematic = thematicService.findById(thematicId);
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            null,
                            new ApiResponse.Meta(
                                    "El ID proporcionado es inválido.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        if( thematic.getData() != null ){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(
                            thematic.getData(),
                            new ApiResponse.Meta(
                                    "Temática obtenido por ID.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        null,
                        new ApiResponse.Meta(
                                thematic.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

}
