package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.DetailThematicComfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateThematicDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateThematicDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.DetailThematicComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ThematicRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ThematicService;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThematicServiceImpl implements ThematicService {

    static String dummiesUser = "usuario123";
    private static final Logger logger = LoggerFactory.getLogger(ThematicServiceImpl.class);
    private final ThematicRepository thematicRepository;
    private final ComfortRepository comfortRepository;
    private final DetailThematicComfortRepository detailThematicComfortRepository;

    @Autowired
    public ThematicServiceImpl(
            ThematicRepository thematicRepository,
            ComfortRepository comfortRepository,
            DetailThematicComfortRepository detailThematicComfortRepository
    ){
        this.thematicRepository = thematicRepository;
        this.comfortRepository = comfortRepository;
        this.detailThematicComfortRepository = detailThematicComfortRepository;
    }

    @Override
    @Transactional
    public ResponseWrapper<Thematic> create(CreateThematicDto thematic) {

        logger.info("Iniciando Acción - Creación de una temática");

        try{

            //? Validemos que no se repita la temática
            String thematicName = thematic.getName().trim().toUpperCase();
            Optional<Thematic> getThematicOptional = thematicRepository.getThematicByName(thematicName);
            if( getThematicOptional.isPresent() )
                return new ResponseWrapper<>(null, "El nombre de la temática ya se encuentra registrado");

            //? Agregamos primero la temática
            Thematic newThematic = new Thematic();
            newThematic.setName(thematicName);
            newThematic.setDescription(thematic.getDescription());
            newThematic.setPopulate(0);
            newThematic.setStatus(true);
            newThematic.setUserCreated(dummiesUser); //! Ajustar cuando se implemente Security
            newThematic.setDateCreated(new Date()); //! Ajustar cuando se implemente Security
            newThematic.setUserUpdated(dummiesUser); //! Ajustar cuando se implemente Security
            newThematic.setDateUpdated(new Date()); //! Ajustar cuando se implemente Security

            Thematic saveThematic = thematicRepository.save(newThematic);

            //? Agregamos los detalles de las comodidades a la temática
            List<Long> forErrorComfort = new ArrayList<>();
            Set<DetailThematicComfort> details = new HashSet<>();
            for (Long comfortId: thematic.getComfortsListId()){

                //? Verifiquémos que el ID de Comodidad Exista y lo Registramos
                Optional<Comfort> getComfortOptional = comfortRepository.findById(comfortId);
                if( getComfortOptional.isPresent() ) {

                    Comfort valueComfort = getComfortOptional.orElseThrow();
                    DetailThematicComfort detailThematic = new DetailThematicComfort();
                    detailThematic.setThematic(saveThematic);
                    detailThematic.setComfort(valueComfort);
                    detailThematic.setUserCreated(dummiesUser); //! Ajustar cuando se implemente Security
                    detailThematic.setDateCreated(new Date()); //! Ajustar cuando se implemente Security
                    DetailThematicComfort detail = detailThematicComfortRepository.save(detailThematic);
                    details.add(detail);

                }else{
                    forErrorComfort.add(comfortId);
                }

            }

            saveThematic.setDetailThematicComforts(details);
            logger.info("Temática creada creada correctamente");
            if(!forErrorComfort.isEmpty()){

                logger.info("Existen comodidades que no pudieron ser registradas, [{}]", forErrorComfort);
                return new ResponseWrapper<>(saveThematic,
                        "Temática guardada correctamente, existen comodidades que no pudieron ser registradas, revise el log");

            }

            return new ResponseWrapper<>(saveThematic,"Temática guardada correctamente");

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar crear la temática, detalles ...", err);
            return new ResponseWrapper<>(null, "La temática no pudo ser creada");

        }

    }

    @Override
    @Transactional(readOnly = true)
    public Page<Thematic> findAll(String search, Pageable pageable) {

        logger.info("Iniciando Acción - Obtener todas las temáticas paginadas y con filtro");
        Specification<Thematic> spec = this.searchByFilter(search);

        logger.info("Listado de temáticas obtenida");
        return thematicRepository.findAll(spec, pageable);

    }

    @Override
    public ResponseWrapper<Thematic> findById(Long id) {

        logger.info("Iniciando Acción - Obteniendo una Temática por ID");

        try{

            Optional<Thematic> planOptional = thematicRepository.getByIdComplementated(id);
            if( planOptional.isPresent() ){
                Thematic thematic = planOptional.orElseThrow();
                logger.info("La temática fue encontrada correctamente dado su ID {}", id);
                return new ResponseWrapper<>(thematic, "Temática encontrada por ID correctamente");
            }

            logger.info("La temática no fue encontrada dado su ID {}", id);
            return new ResponseWrapper<>(null, "La temática no pudo ser encontrado por el ID " + id);

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar obtener la temática por ID {}, detalles: ", id, err);
            return new ResponseWrapper<>(null, "La temática no pudo ser encontrado por el ID");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Object> update(Long id, UpdateThematicDto thematic) {

        logger.info("Iniciando Acción - Actualizar una temática dado su ID");

        try{

            Optional<Thematic> thematicOptional = thematicRepository.getByIdComplementated(id);
            if( thematicOptional.isPresent() ){

                Thematic thematicDb = thematicOptional.orElseThrow();

                //? Validemos que no se repita la comodidad
                String thematicName = thematic.getName().trim().toUpperCase();
                Optional<Thematic> getThematicOptionalName = thematicRepository.getThematicByNameForEdit(thematicName, id);

                if( getThematicOptionalName.isPresent() ){
                    logger.info("La temática no se puede actualizar porque el nombre ya está registrado");
                    return new ResponseWrapper<>(null, "El nombre de la temática ya está registrado");
                }

                //? Vamos a actualizar si llegamos hasta acá
                thematicDb.setName(thematicName);
                thematicDb.setDescription(thematic.getDescription());
                thematicDb.setUserUpdated(dummiesUser);
                thematicDb.setDateUpdated(new Date());

                Thematic saveThematic = thematicRepository.save(thematicDb);

                //? Ahora, vamos a reingresar las comodidades
                //* Borremos las que teníamos
                thematicRepository.deleteDetailThematicComfortsByThematicId(id);

                //* Ingresemos nuevamente
                List<Long> forErrorComfort = new ArrayList<>();
                Set<DetailThematicComfort> details = new HashSet<>();
                for (Long comfortId: thematic.getComfortsListId()){

                    //? Verifiquémos que el ID de Comodidad Exista y lo Registramos
                    Optional<Comfort> getComfortOptional = comfortRepository.findById(comfortId);
                    if( getComfortOptional.isPresent() ) {

                        Comfort valueComfort = getComfortOptional.orElseThrow();
                        DetailThematicComfort detailThematic = new DetailThematicComfort();
                        detailThematic.setThematic(saveThematic);
                        detailThematic.setComfort(valueComfort);
                        detailThematic.setUserCreated(dummiesUser); //! Ajustar cuando se implemente Security
                        detailThematic.setDateCreated(new Date()); //! Ajustar cuando se implemente Security
                        DetailThematicComfort detail = detailThematicComfortRepository.save(detailThematic);
                        details.add(detail);

                    }else{
                        forErrorComfort.add(comfortId);
                    }

                }

                saveThematic.setDetailThematicComforts(details);
                logger.info("Temática actualizada correctamente");
                if(!forErrorComfort.isEmpty()){

                    logger.info("Existen comodidades que no pudieron ser registradas en la actualización, [{}]", forErrorComfort);
                    return new ResponseWrapper<>(saveThematic,
                            "Temática actualizada correctamente, existen comodidades que no pudieron ser registradas, revise el log");

                }

                logger.info("La temática fue actualizada correctamente");
                return new ResponseWrapper<>(thematicRepository.getByIdComplementated(id), "Temática Actualizada Correctamente");

            }else{

                return new ResponseWrapper<>(null, "La temática no fue encontrada");

            }

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar actualizar temática por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La temática no pudo ser actualizada");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Object> delete(Long id) {

        try{

            Optional<Thematic> thematicOptional = thematicRepository.getByIdComplementated(id);

            if( thematicOptional.isPresent() ){

                Thematic thematicDb = thematicOptional.orElseThrow();

                //? Vamos a actualizar si llegamos hasta acá
                //? ESTO SERÁ UN ELIMINADO LÓGICO!
                thematicDb.setStatus(false);
                thematicDb.setUserUpdated("usuario123");
                thematicDb.setDateUpdated(new Date());

                return new ResponseWrapper<>(thematicRepository.save(thematicDb), "Temática Eliminada Correctamente");

            }else{

                return new ResponseWrapper<>(null, "La temática no fue encontrado");

            }

        }catch (Exception err) {

            logger.error("Ocurrió un error al intentar eliminar lógicamente temática por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La temática no pudo ser eliminada");

        }

    }

    //* Para el buscador de temáticas.
    //? Buscarémos tanto por name como por description.
    //? NOTA: No olvidar el status.
    public Specification<Thematic> searchByFilter(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.isTrue(root.get("status"));
            }

            String searchPattern = "%" + search.toLowerCase() + "%"; //También podría ser toLowerCase o simplemente "%" + search + "%"

            // Aplico Join para buscar por entidad relacionada Room
            Join<Thematic, Room> roomJoin = root.join("rooms", JoinType.LEFT);

            return criteriaBuilder.and(
                    criteriaBuilder.isTrue(root.get("status")),
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern),
                            criteriaBuilder.like(criteriaBuilder.lower(roomJoin.get("name")), searchPattern)
                    )
            );
        };
    }

}
