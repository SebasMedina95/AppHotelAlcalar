package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.services;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.common.utils.ResponseWrapper;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateComfortDto;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.update.UpdateComfortDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComfortService {

    ResponseWrapper<Comfort> create(CreateComfortDto comfort);
    Page<Comfort> findAll(String search, Pageable pageable);
    ResponseWrapper<Comfort> findById(Long id);
    ResponseWrapper<Comfort> update(Long id, UpdateComfortDto thematic);
    ResponseWrapper<Comfort> delete(Long id);

}
