package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateThematicDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateThematicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThematicService {

    ResponseWrapper<Thematic> create(CreateThematicDto thematic);
    Page<Thematic> findAll(String search, Pageable pageable);
    ResponseWrapper<Thematic> findById(Long id);
    ResponseWrapper<Object> update(Long id, UpdateThematicDto thematic);
    ResponseWrapper<Object> delete(Long id);

}
