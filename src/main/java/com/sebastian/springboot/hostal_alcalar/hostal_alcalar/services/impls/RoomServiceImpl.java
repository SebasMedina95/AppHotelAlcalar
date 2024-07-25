package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.others.GenericRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateRoomDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.RoomRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ThematicRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.RoomService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {

    static String dummiesUser = "usuario123";
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
    private final RoomRepository roomRepository;
    private final ThematicRepository thematicRepository;

    @Autowired
    public RoomServiceImpl(
            RoomRepository roomRepository,
            ThematicRepository thematicRepository
    ){
        this.roomRepository = roomRepository;
        this.thematicRepository = thematicRepository;
    }

    @Override
    @Transactional
    public ResponseWrapper<Room> create(CreateRoomDto room) {

        logger.info("Iniciando Acción - Creación de una habitación");

        try{

            //? Validemos que no se repita la temática
            String roomName = room.getName().trim().toUpperCase();
            Optional<Room> getRoomOptional = roomRepository.getRoomByName(roomName);
            if( getRoomOptional.isPresent() )
                return new ResponseWrapper<>(null, "El nombre de la habitación ya se encuentra registrado");

            //? Validemos la temática que nos están proporcionando
            Optional<Thematic> getThematicOptional = thematicRepository.findById(room.getThematicId());
            if(getThematicOptional.isEmpty())
                return new ResponseWrapper<>(null, "La temática a la que está intentando asociar la habitación no existe");

            Thematic getThematic = getThematicOptional.orElseThrow();

            //? Agregamos primero la temática
            Room newRoom = new Room();
            newRoom.setName(roomName);
            newRoom.setMaintenance(room.getMaintenance());
            newRoom.setStatus(true);
            newRoom.setThematic(getThematic);
            newRoom.setUserCreated(dummiesUser); //! Ajustar cuando se implemente Security
            newRoom.setDateCreated(new Date()); //! Ajustar cuando se implemente Security
            newRoom.setUserUpdated(dummiesUser); //! Ajustar cuando se implemente Security
            newRoom.setDateUpdated(new Date()); //! Ajustar cuando se implemente Security

            Room savedRoom = roomRepository.save(newRoom);
            logger.info("Habitación creada creada correctamente");
            return new ResponseWrapper<>(savedRoom,"Habitación guardada correctamente");

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar crear la habitación, detalles ...", err);
            return new ResponseWrapper<>(null, "La habitación no pudo ser creada");

        }

    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenericRoomDto> findAll(String search, Pageable pageable) {

        logger.info("Iniciando Acción - Obtener todas las habitaciones paginadas y con filtro");
        Specification<Room> spec = this.searchByFilter(search);
        Page<Room> result = roomRepository.findAll(spec, pageable);

        List<GenericRoomDto> finalResponse = new ArrayList<>();
        result.forEach(room -> {
            GenericRoomDto newDto = new GenericRoomDto();
            newDto.setId(room.getId());
            newDto.setName(room.getName());
            newDto.setMaintenance(room.getMaintenance());
            newDto.setStatus(room.getStatus());
            newDto.setUserCreated(room.getUserCreated());
            newDto.setDateCreated(room.getDateCreated());
            newDto.setUserUpdated(room.getUserUpdated());
            newDto.setDateUpdated(room.getDateUpdated());

            if (room.getThematic() != null) {
                Thematic thematic = thematicRepository
                        .findById(room.getThematic().getId()).orElseThrow();

                newDto.setThematicName(thematic.getName());

            }

            finalResponse.add(newDto);
        });

        // Crea un objeto Page a partir de la lista de DTOS
        Page<GenericRoomDto> paginatedResponse = new PageImpl<>(finalResponse, pageable, result.getTotalElements());

        logger.info("Listado de habitaciones obtenida");
        return paginatedResponse;

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseWrapper<GenericRoomDto> findById(Long id) {

        logger.info("Iniciando Acción - Obteniendo una Habitación por ID");

        try{

            Optional<Room> roomOptional = roomRepository.findById(id);
            if( roomOptional.isPresent() ){
                Room room = roomOptional.orElseThrow();

                GenericRoomDto finalResponse = new GenericRoomDto();
                finalResponse.setId(room.getId());
                finalResponse.setName(room.getName());
                finalResponse.setMaintenance(room.getMaintenance());
                finalResponse.setStatus(room.getStatus());
                finalResponse.setThematicName(room.getThematic().getName());
                finalResponse.setUserCreated(room.getUserCreated());
                finalResponse.setDateCreated(room.getDateCreated());
                finalResponse.setUserUpdated(room.getUserUpdated());
                finalResponse.setDateUpdated(room.getDateUpdated());

                logger.info("La habitación fue encontrada correctamente dado su ID {}", id);
                return new ResponseWrapper<>(finalResponse, "Habitación encontrada por ID correctamente");
            }

            logger.info("La habitación no fue encontrada dado su ID {}", id);
            return new ResponseWrapper<>(null, "La habitación no pudo ser encontrado por el ID " + id);

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar obtener la habitación por ID {}, detalles: ", id, err);
            return new ResponseWrapper<>(null, "La habitación no pudo ser encontrado por el ID");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Room> update(Long id, UpdateRoomDto room) {

        logger.info("Iniciando Acción - Actualizar una habitación dado su ID");

        try{

            Optional<Room> roomOptional = roomRepository.findById(id);
            if( roomOptional.isPresent() ){

                Room roomDb = roomOptional.orElseThrow();

                //? Validemos que no se repita la habitación
                String roomName = room.getName().trim().toUpperCase();
                Optional<Room> getRoomOptionalName = roomRepository.getRoomByNameForEdit(roomName, id);

                if( getRoomOptionalName.isPresent() ){
                    logger.info("La habitación no se puede actualizar porque el nombre ya está registrado");
                    return new ResponseWrapper<>(null, "El nombre de la habitación ya está registrado");
                }

                //? Validemos la temática que nos están proporcionando
                Optional<Thematic> getThematicOptional = thematicRepository.findById(room.getThematicId());
                if(getThematicOptional.isEmpty())
                    return new ResponseWrapper<>(null, "La temática a la que está intentando asociar la habitación no existe");

                Thematic getThematic = getThematicOptional.orElseThrow();

                //? Vamos a actualizar si llegamos hasta acá
                roomDb.setName(roomName);
                roomDb.setMaintenance(room.getMaintenance());
                roomDb.setThematic(getThematic);
                roomDb.setUserUpdated(dummiesUser);
                roomDb.setDateUpdated(new Date());

                logger.info("La habitación fue actualizada correctamente");
                return new ResponseWrapper<>(roomRepository.save(roomDb), "Habitación Actualizada Correctamente");

            }else{

                return new ResponseWrapper<>(null, "La habitación no fue encontrada");

            }

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar actualizar temática por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La temática no pudo ser actualizada");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Room> delete(Long id) {

        try{

            Optional<Room> roomOptional = roomRepository.findById(id);

            if( roomOptional.isPresent() ){

                Room roomDb = roomOptional.orElseThrow();

                //? Vamos a actualizar si llegamos hasta acá
                //? ESTO SERÁ UN ELIMINADO LÓGICO!
                roomDb.setStatus(false);
                roomDb.setUserUpdated("usuario123");
                roomDb.setDateUpdated(new Date());

                return new ResponseWrapper<>(roomRepository.save(roomDb), "Habitación Eliminada Correctamente");

            }else{

                return new ResponseWrapper<>(null, "La habitación no fue encontrado");

            }

        }catch (Exception err) {

            logger.error("Ocurrió un error al intentar eliminar lógicamente habitación por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La habitación no pudo ser eliminada");

        }

    }

    //* Para el buscador de temáticas.
    //? Buscarémos tanto por name como por description.
    //? NOTA: No olvidar el status.
    public Specification<Room> searchByFilter(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.isTrue(root.get("status"));
            }

            String searchPattern = "%" + search.toLowerCase() + "%";

            // Crear sub consulta para buscar por thematicName
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Thematic> thematicRoot = subquery.from(Thematic.class);
            subquery.select(thematicRoot.get("id")).where(
                    criteriaBuilder.like(criteriaBuilder.lower(thematicRoot.get("name")), searchPattern)
            );

            Join<Room, Thematic> thematicJoin = root.join("thematic", JoinType.LEFT);

            return criteriaBuilder.and(
                    criteriaBuilder.isTrue(root.get("status")),
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                            criteriaBuilder.in(thematicJoin.get("id")).value(subquery)
                    )
            );
        };
    }

}
