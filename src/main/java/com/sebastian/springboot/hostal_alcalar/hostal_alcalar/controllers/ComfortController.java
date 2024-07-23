package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.controllers;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.dtos.PaginationDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ApiResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.CustomPagedResourcesAssembler;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ErrorsValidationsResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ComfortService;
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
@RequestMapping("/api/comforts")
@Tag(name = "Controlador de Comodidades", description = "Operaciones relacionadas con las comodidades")
public class ComfortController {

    private final ComfortService comfortService;
    private final CustomPagedResourcesAssembler<Comfort> customPagedResourcesAssembler;

    @Autowired
    public ComfortController(
            ComfortService comfortService,
            CustomPagedResourcesAssembler<Comfort> customPagedResourcesAssembler){
        this.comfortService = comfortService;
        this.customPagedResourcesAssembler = customPagedResourcesAssembler;
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una Comodidad", description = "Creación de una comodidad")
    public ResponseEntity<ApiResponse<Object>> create(
            @Valid
            @RequestBody CreateComfortDto comfortRequest,
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
        ResponseWrapper<Comfort> comfortNew = comfortService.create(comfortRequest);

        //? Si no ocurre algún error, entonces registramos :)
        if( comfortNew.getData() != null ){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            comfortNew.getData(),
                            new ApiResponse.Meta(
                                    "Comodidad Registrada Correctamente.",
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
                                comfortNew.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

    @GetMapping("/find-all")
    @Operation(summary = "Obtener todas las comodidades", description = "Obtener todas las comodidades con paginación y también aplicando filtros")
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
                                    "Errores en los campos",
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        if (paginationDto.getPage() < 1) paginationDto.setPage(1); //Para controlar la página 0, y que la paginación arranque en 1.

        Sort.Direction direction = paginationDto.getOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(paginationDto.getPage() - 1, paginationDto.getSize(), Sort.by(direction, paginationDto.getSort())); //Generando el esquema de paginación para aplicar y ordenamiento
        Page<Comfort> comforts = comfortService.findAll(paginationDto.getSearch(), pageable); //Aplicando la paginación JPA -> Incorporo el buscador
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri(); //Para la obtención de la URL

        PagedModel<Comfort> pagedModel = customPagedResourcesAssembler.toModel(comforts, uriBuilder);

        return ResponseEntity.ok(new ApiResponse<>(
                pagedModel,
                new ApiResponse.Meta(
                        "Listado de comodidades.",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        ));

    }

    @GetMapping("/find-by-id/{id}")
    @Operation(summary = "Obtener comodidades por ID", description = "Obtener una comodidad dado el ID")
    public ResponseEntity<ApiResponse<Comfort>> findById(@PathVariable String id){

        ResponseWrapper<Comfort> comfort;

        //Validamos que el ID que nos proporcionan por la URL sea válido
        try {
            Long comfortId = Long.parseLong(id);
            comfort = comfortService.findById(comfortId);
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            null,
                            new ApiResponse.Meta(
                                    "El ID proporcionado para la búsqueda es inválido.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        if( comfort.getData() != null ){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(
                            comfort.getData(),
                            new ApiResponse.Meta(
                                    "Comodidad obtenida por ID.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        null,
                        new ApiResponse.Meta(
                                comfort.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

    @PutMapping("update-by-id/{id}")
    @Operation(summary = "Actualizar una comodidad", description = "Actualizar una comodidad dado el ID")
    public ResponseEntity<ApiResponse<Object>> update(
            @Valid
            @RequestBody UpdateComfortDto comfortRequest,
            BindingResult result,
            @PathVariable String id
    ){

        if(result.hasFieldErrors()){
            ErrorsValidationsResponse errors = new ErrorsValidationsResponse();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            errors.validation(result),
                            new ApiResponse.Meta(
                                    "Errores en los campos de actualización",
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        ResponseWrapper<Comfort> comfortUpdate;

        try {
            Long comfortId = Long.parseLong(id);
            comfortUpdate = comfortService.update(comfortId, comfortRequest);
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

        if( comfortUpdate.getData() != null ){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(
                            comfortUpdate.getData(),
                            new ApiResponse.Meta(
                                    "Comodidad Actualizada Correctamente.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        null,
                        new ApiResponse.Meta(
                                comfortUpdate.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

    @DeleteMapping("/delete-by-id/{id}")
    @Operation(summary = "Eliminar un plan", description = "Eliminar un plan pero de manera lógica")
    public ResponseEntity<ApiResponse<Comfort>> delete(@PathVariable String id){

        ResponseWrapper<Comfort> comfortUpdate;

        try {
            Long comfortId = Long.parseLong(id);
            comfortUpdate = comfortService.delete(comfortId);
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

        if( comfortUpdate.getData() != null ){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(
                            comfortUpdate.getData(),
                            new ApiResponse.Meta(
                                    "Comodidad Eliminada Correctamente.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        null,
                        new ApiResponse.Meta(
                                comfortUpdate.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

}
