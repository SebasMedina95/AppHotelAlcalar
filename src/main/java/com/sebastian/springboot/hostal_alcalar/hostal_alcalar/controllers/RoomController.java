package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.controllers;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.dtos.PaginationDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ApiResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.CustomPagedResourcesAssembler;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ErrorsValidationsResponse;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.others.GenericRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.RoomService;
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
@RequestMapping("/api/rooms")
@Tag(name = "Controlador de Habitaciones", description = "Operaciones relacionadas con las habitaciones")
public class RoomController {

    private final RoomService roomService;
    private final CustomPagedResourcesAssembler<GenericRoomDto> customPagedResourcesAssembler;

    @Autowired
    public RoomController(
            RoomService roomService,
            CustomPagedResourcesAssembler<GenericRoomDto> customPagedResourcesAssembler){
        this.roomService = roomService;
        this.customPagedResourcesAssembler = customPagedResourcesAssembler;
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una Temática", description = "Creación de una temática")
    public ResponseEntity<ApiResponse<Object>> create(
            @Valid
            @RequestBody CreateRoomDto roomRequest,
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
        ResponseWrapper<Room> roomNew = roomService.create(roomRequest);

        //? Si no ocurre algún error, entonces registramos :)
        if( roomNew.getData() != null ){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            roomNew.getData(),
                            new ApiResponse.Meta(
                                    "Habitación Registrada Correctamente.",
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
                                roomNew.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

    @GetMapping("/find-all")
    @Operation(summary = "Obtener todas las habitaciones", description = "Obtener todas las habitaciones con paginación y también aplicando filtros")
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
        Page<GenericRoomDto> rooms = roomService.findAll(paginationDto.getSearch(), pageable); //Aplicando la paginación JPA -> Incorporo el buscador
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri(); //Para la obtención de la URL

        PagedModel<GenericRoomDto> pagedModel = customPagedResourcesAssembler.toModel(rooms, uriBuilder);

        return ResponseEntity.ok(new ApiResponse<>(
                pagedModel,
                new ApiResponse.Meta(
                        "Listado de temáticas.",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        ));

    }

}
