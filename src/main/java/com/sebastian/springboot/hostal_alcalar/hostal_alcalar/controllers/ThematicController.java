package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.controllers;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.dtos.PaginationDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ApiResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.CustomPagedResourcesAssembler;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ErrorsValidationsResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateThematicDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ThematicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PostMapping("/create")
    @Operation(summary = "Crear una Temática", description = "Creación de una temática")
    public ResponseEntity<ApiResponse<Object>> create(
            @Valid
            @RequestBody CreateThematicDto thematicRequest,
            BindingResult result
    ){

        //Validación de campos
        if(result.hasFieldErrors()){
            ErrorsValidationsResponse errors = new ErrorsValidationsResponse();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            errors.validation(result),
                            new ApiResponse.Meta(
                                    "Errores en los campos de creación",
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        //? Intentamos realizar el registro
        ResponseWrapper<Thematic> thematicNew = thematicService.create(thematicRequest);

        //? Si no ocurre algún error, entonces registramos :)
        if( thematicNew.getData() != null ){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            thematicNew.getData(),
                            new ApiResponse.Meta(
                                    "Temática Registrada Correctamente.",
                                    HttpStatus.CREATED.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        //? Estamos en este punto, el registro no fue correcto
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        null,
                        new ApiResponse.Meta(
                                thematicNew.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

    @GetMapping("/find-all")
    @Operation(summary = "Obtener todas las temáticas", description = "Obtener todas las temáticas con paginación y también aplicando filtros")
    public ResponseEntity<ApiResponse<Object>> findAll(
            @Valid
            @RequestBody PaginationDto paginationDto,
            BindingResult result
    ){

        //Validación de campos
        if(result.hasFieldErrors()){
            ErrorsValidationsResponse errors = new ErrorsValidationsResponse();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            errors.validation(result),
                            new ApiResponse.Meta(
                                    "Errores en los campos de paginación",
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        if (paginationDto.getPage() < 1) paginationDto.setPage(1); //Para controlar la página 0, y que la paginación arranque en 1.

        Sort.Direction direction = paginationDto.getOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(paginationDto.getPage() - 1, paginationDto.getSize(), Sort.by(direction, paginationDto.getSort())); //Generando el esquema de paginación para aplicar y ordenamiento
        Page<Thematic> comforts = thematicService.findAll(paginationDto.getSearch(), pageable); //Aplicando la paginación JPA -> Incorporo el buscador
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri(); //Para la obtención de la URL

        PagedModel<Thematic> pagedModel = customPagedResourcesAssembler.toModel(comforts, uriBuilder);

        return ResponseEntity.ok(new ApiResponse<>(
                pagedModel,
                new ApiResponse.Meta(
                        "Listado de temáticas.",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        ));

    }

    @GetMapping("/find-by-id/{id}")
    @Operation(summary = "Obtener temáticas por ID", description = "Obtener una temática dado el ID")
    public ResponseEntity<ApiResponse<Thematic>> findById(@PathVariable String id){

        ResponseWrapper<Thematic> thematic;

        //Validamos que el ID que nos proporcionan por la URL sea válido
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

        //Si es diferente de null implica que lo encontramos
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

        //En caso contrario, algún null, ocurrió un error
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
