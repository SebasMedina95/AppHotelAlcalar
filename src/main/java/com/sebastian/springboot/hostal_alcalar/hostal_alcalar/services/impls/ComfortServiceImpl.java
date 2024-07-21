package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ComfortRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ComfortService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    public ResponseWrapper<Comfort> create(CreateComfortDto comfort) {

        logger.info("Iniciando Acción - Creación de una comodidad");

        try{

            //? Validemos que no se repita la plaza
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
    public Page<Comfort> findAll(String search, Pageable pageable) {

        Specification<Comfort> spec = this.searchByFilter(search);
        return comfortRepository.findAll(spec, pageable);

    }

    @Override
    public ResponseWrapper<Comfort> findById(Long id) {

        Optional<Comfort> comfortOptional = comfortRepository.findById(id);
        if( comfortOptional.isPresent() ){
            Comfort comfort = comfortOptional.orElseThrow();
            return new ResponseWrapper<>(comfort, "Comodidad encontrada por ID correctamente");
        }

        return new ResponseWrapper<>(null, "La comodidad no pudo ser encontrado por el ID " + id);


    }

    @Override
    public ResponseWrapper<Comfort> update(Long id, Comfort thematic) {
        return null;
    }

    @Override
    public ResponseWrapper<Comfort> delete(Long id) {
        return null;
    }

    //* Para el buscador de planes.
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
