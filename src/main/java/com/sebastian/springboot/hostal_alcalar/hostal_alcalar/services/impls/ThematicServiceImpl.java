package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.impls;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.repositories.ThematicRepository;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services.ThematicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ThematicServiceImpl implements ThematicService {

    static String dummiesUser = "usuario123";
    private static final Logger logger = LoggerFactory.getLogger(ThematicServiceImpl.class);
    private final ThematicRepository thematicRepository;

    @Autowired
    public ThematicServiceImpl(
            ThematicRepository thematicRepository
    ){
        this.thematicRepository = thematicRepository;
    }

    @Override
    public ResponseWrapper<Thematic> create(Thematic thematic) {
        return null;
    }

    @Override
    public Page<Thematic> findAll(String search, Pageable pageable) {
        return null;
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
    public ResponseWrapper<Thematic> update(Long id, Thematic thematic) {
        return null;
    }

    @Override
    public ResponseWrapper<Thematic> delete(Long id) {
        return null;
    }
}
