package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateComfortDto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ComfortMock {

    static String user = "123456789";
    public static final Comfort comfort1 = new Comfort(1L, "Sofá Comodidad 1", true, "icon1", user, new Date(), user, new Date());
    public static final Comfort comfort2 = new Comfort(2L, "Cama Comodidad 2", true, "icon2", user, new Date(), user, new Date());
    public static final Comfort comfort3 = new Comfort(4L, "Jacuzzi Comodidad 4", true, "icon4", user, new Date(), user, new Date());
    public static final Comfort comfort4 = new Comfort(5L, "Bar Comodidad 5", true, "icon5", user, new Date(), user, new Date());
    public static final Comfort comfort5 = new Comfort(6L, "Silla del Amor Comodidad 6", true, "icon6", user, new Date(), user, new Date());

    public static CreateComfortDto createComfortDto() {
        CreateComfortDto comfortDto = new CreateComfortDto();
        comfortDto.setName("Test Comfort");
        comfortDto.setIcon("<i class='fa-solid fa-bath'></i>");
        return comfortDto;
    }

    public static Comfort createComfort() {
        Comfort comfort = new Comfort();
        comfort.setName("TEST COMFORT");
        comfort.setIcon("<i class='fa-solid fa-bath'></i>");
        comfort.setStatus(true);
        comfort.setDateCreated(new Date());
        comfort.setDateUpdated(new Date());
        comfort.setUserCreated("123456789");
        comfort.setUserUpdated("123456789");
        return comfort;
    }

    public static List<Comfort> comfortList(){
        Comfort comfort1 = new Comfort();
        comfort1.setName("Comfort1");
        comfort1.setIcon("Example Icon 1");
        comfort1.setStatus(true);

        Comfort comfort2 = new Comfort();
        comfort2.setName("Comfort2");
        comfort2.setIcon("Example Icon 2");
        comfort2.setStatus(true);

        Comfort comfort3 = new Comfort();
        comfort3.setName("Comfort3");
        comfort3.setIcon("Example Icon 3");
        comfort3.setStatus(true);

        List<Comfort> newList = Arrays.asList(comfort1, comfort2, comfort3);
        return newList;

    }

}
