package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.others;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GenericRoomDto {

    private Long id;
    private String name;
    private String thematicName;
    private String maintenance;
    private Boolean status;
    private String userCreated;
    private Date dateCreated;
    private String userUpdated;
    private Date dateUpdated;

}
