package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ComfortService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ComfortServiceImpl implements ComfortService {

    static String dummiesUser = "usuario123";
    private static final Logger logger = LoggerFactory.getLogger(ComfortServiceImpl.class);
    private final ComfortRepository comfortRepository;

    @Autowired
    public ComfortServiceImpl(
            ComfortRepository comfortRepository
    ){
        this.comfortRepository = comfortRepository;
    }
    @Override
    @Transactional
    public ResponseWrapper<Comfort> create(CreateComfortDto comfort) {

        logger.info("Iniciando Acción - Creación de una comodidad");

        try{

            //? Validemos que no se repita la comodidad
            String comfortName = comfort.getName().trim().toUpperCase();
            Optional<Comfort> getComfortOptional = comfortRepository.getComfortByName(comfortName);
            if( getComfortOptional.isPresent() )
                return new ResponseWrapper<>(null, "El nombre de la comodidad ya se encuentra registrado");

            //? Pasamos hasta acá, registramos plaza
            Comfort newComfort = new Comfort();
            newComfort.setName(comfortName);
            newComfort.setIcon(comfort.getIcon());
            newComfort.setStatus(true); //* Por defecto entra en true
            newComfort.setUserCreated(dummiesUser); //! Ajustar cuando se implemente Security
            newComfort.setDateCreated(new Date()); //! Ajustar cuando se implemente Security
            newComfort.setUserUpdated(dummiesUser); //! Ajustar cuando se implemente Security
            newComfort.setDateUpdated(new Date()); //! Ajustar cuando se implemente Security

            logger.info("Comodidad creada correctamente");
            return new ResponseWrapper<>(comfortRepository.save(newComfort), "Comfort guardado correctamente");

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar crear la comodidad, detalles ...", err);
            return new ResponseWrapper<>(null, "La comodidad no pudo ser creada");

        }

    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comfort> findAll(String search, Pageable pageable) {

        logger.info("Iniciando Acción - Obtener todas las comodidades paginadas y con filtro");
        Specification<Comfort> spec = this.searchByFilter(search);

        logger.info("Listado de comodidades obtenida");
        return comfortRepository.findAll(spec, pageable);

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseWrapper<Comfort> findById(Long id) {

        logger.info("Iniciando Acción - Obtener una comodidad dado su ID");

        try{

            Optional<Comfort> comfortOptional = comfortRepository.findById(id);

            if( comfortOptional.isPresent() ){
                Comfort comfort = comfortOptional.orElseThrow();
                logger.info("Comodidad obtenida por su ID");
                return new ResponseWrapper<>(comfort, "Comodidad encontrada por ID correctamente");
            }

            logger.info("La comodidad no pudo ser encontrada cone el ID {}", id);
            return new ResponseWrapper<>(null, "La comodidad no pudo ser encontrado por el ID " + id);

        }catch (Exception err) {

            logger.error("Ocurrió un error al intentar obtener comodidad por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La comodidad no pudo ser encontrado por el ID");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Comfort> update(Long id, UpdateComfortDto comfort) {

        logger.info("Iniciando Acción - Actualizar una comodidad dado su ID");

        try{

            Optional<Comfort> comfortOptional = comfortRepository.findById(id);
            if( comfortOptional.isPresent() ){

                Comfort comfortDb = comfortOptional.orElseThrow();

                //? Validemos que no se repita la comodidad
                String comfortName = comfort.getName().trim().toUpperCase();
                Optional<Comfort> getComfortOptionalName = comfortRepository.getComfortByNameForEdit(comfortName, id);

                if( getComfortOptionalName.isPresent() ){
                    logger.info("La comodidad no se puede actualizar porque el nombre ya está registrado");
                    return new ResponseWrapper<>(null, "El nombre de la comodidad ya está registrado");
                }

                //? Vamos a actualizar si llegamos hasta acá
                comfortDb.setName(comfortName);
                comfortDb.setIcon(comfort.getIcon());
                comfortDb.setUserUpdated(dummiesUser);
                comfortDb.setDateUpdated(new Date());

                logger.info("La comodidad fue actualizada correctamente");
                return new ResponseWrapper<>(comfortRepository.save(comfortDb), "Comodidad Actualizada Correctamente");

            }else{

                return new ResponseWrapper<>(null, "La comodidad no fue encontrada");

            }

        }catch (Exception err){

            logger.error("Ocurrió un error al intentar actualizar comodidad por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La comodidad no pudo ser actualizada");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Comfort> delete(Long id) {

        try{

            Optional<Comfort> comfortOptional = comfortRepository.findById(id);

            if( comfortOptional.isPresent() ){

                Comfort comfortDb = comfortOptional.orElseThrow();

                //? Vamos a actualizar si llegamos hasta acá
                //? ESTO SERÁ UN ELIMINADO LÓGICO!
                comfortDb.setStatus(false);
                comfortDb.setUserUpdated("usuario123");
                comfortDb.setDateUpdated(new Date());

                return new ResponseWrapper<>(comfortRepository.save(comfortDb), "Comodidad Eliminada Correctamente");

            }else{

                return new ResponseWrapper<>(null, "La comodidad no fue encontrado");

            }

        }catch (Exception err) {

            logger.error("Ocurrió un error al intentar eliminar lógicamente comodidad por ID, detalles ...", err);
            return new ResponseWrapper<>(null, "La comodidad no pudo ser eliminada");

        }

    }

    //* Para el buscador de comodidades.
    //? Buscarémos tanto por name como por description.
    //? NOTA: No olvidar el status.
    public Specification<Comfort> searchByFilter(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.isTrue(root.get("status"));
            }

            String searchPatternWithUpper = "%" + search.toUpperCase() + "%"; //También podría ser toLowerCase o simplemente "%" + search + "%"

            return criteriaBuilder.and(
                    criteriaBuilder.isTrue(root.get("status")),
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"), searchPatternWithUpper)
                    )
            );
        };
    }

}
