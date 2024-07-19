package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThematicService {

    ResponseWrapper<Thematic> create(Thematic thematic); //Debemos crear el CreateThematicDto
    Page<Thematic> findAll(String search, Pageable pageable);
    ResponseWrapper<Thematic> findById(Long id);
    ResponseWrapper<Thematic> update(Long id, Thematic thematic); //Debemos crear el UpdateThematicDto
    ResponseWrapper<Thematic> delete(Long id);

}
