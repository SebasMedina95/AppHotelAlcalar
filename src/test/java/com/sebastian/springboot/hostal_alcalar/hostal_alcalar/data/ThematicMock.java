package com.sebastian.springboot.hostal_alcalar.hostal_alcalar.data;

import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Comfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.DetailThematicComfort;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Room;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.Thematic;
import com.sebastian.springboot.hostal_alcalar.hostal_alcalar.entities.dtos.create.CreateThematicDto;

import java.util.*;

public class ThematicMock {

    static String user = "123456789";

    // Crear objetos Room
    public static final Room room1 = new Room(1L, "Test1-003", "NO", true, user, new Date(), user, new Date(), null);
    public static final Room room2 = new Room(2L, "Test1-002", "NO", true, user, new Date(), user, new Date(), null);
    public static final Room room3 = new Room(3L, "Test1-001", "NO", true, user, new Date(), user, new Date(), null);
    public static final Room room4 = new Room(4L, "Test1-004", "NO", true, user, new Date(), user, new Date(), null);
    public static final Room room5 = new Room(5L, "Test1-005", "NO", true, user, new Date(), user, new Date(), null);
    public static final Room room6 = new Room(6L, "Test1-006", "NO", true, user, new Date(), user, new Date(), null);
    public static final Room room7 = new Room(7L, "Test1-007", "NO", true, user, new Date(), user, new Date(), null);

    // Crear objetos Comfort
    public static final Comfort comfort1 = new Comfort(1L, "Test Comodidad 1", true, "icon1", user, new Date(), user, new Date());
    public static final Comfort comfort2 = new Comfort(2L, "Test Comodidad 2", true, "icon2", user, new Date(), user, new Date());
    public static final Comfort comfort3 = new Comfort(4L, "Test Comodidad 4", true, "icon4", user, new Date(), user, new Date());
    public static final Comfort comfort4 = new Comfort(5L, "Test Comodidad 5", true, "icon5", user, new Date(), user, new Date());
    public static final Comfort comfort5 = new Comfort(6L, "Test Comodidad 6", true, "icon6", user, new Date(), user, new Date());
    public static final Comfort comfort6 = new Comfort(7L, "Test Comodidad 7", true, "icon7", user, new Date(), user, new Date());
    public static final Comfort comfort7 = new Comfort(8L, "Test Comodidad 8", true, "icon8", user, new Date(), user, new Date());
    public static final Comfort comfort8 = new Comfort(9L, "Test Comodidad 9", true, "icon9", user, new Date(), user, new Date());
    public static final Comfort comfort9 = new Comfort(10L, "Test Comodidad 10", true, "icon10", user, new Date(), user, new Date());
    public static final Comfort comfort10 = new Comfort(11L, "Test Comodidad 11", true, "icon11", user, new Date(), user, new Date());
    public static final Comfort comfort11 = new Comfort(12L, "Test Comodidad 12", true, "icon12", user, new Date(), user, new Date());
    public static final Comfort comfort12 = new Comfort(13L, "Test Comodidad 13", true, "icon13", user, new Date(), user, new Date());

    // Inicialización de objetos DetailThematicComfort y Thematic
    public static final DetailThematicComfort comfortDetail1;
    public static final DetailThematicComfort comfortDetail2;
    public static final DetailThematicComfort comfortDetail3;
    public static final DetailThematicComfort comfortDetail4;
    public static final DetailThematicComfort comfortDetail5;
    public static final DetailThematicComfort comfortDetail6;
    public static final DetailThematicComfort comfortDetail7;
    public static final DetailThematicComfort comfortDetail8;
    public static final DetailThematicComfort comfortDetail9;
    public static final DetailThematicComfort comfortDetail10;
    public static final DetailThematicComfort comfortDetail11;
    public static final DetailThematicComfort comfortDetail12;
    public static final DetailThematicComfort comfortDetail13;
    public static final DetailThematicComfort comfortDetail14;

    public static final Thematic thematic1;
    public static final Thematic thematic2;
    public static final Thematic thematic3;

    static {
        // Primero se crea las instancias de Thematic sin los detalles
        thematic1 = new Thematic(
                1L,
                "Test Temática 1",
                "Descripción de Test para la Temática 1",
                1092,
                true,
                user,
                new Date(),
                user,
                new Date(),
                new HashSet<>(Set.of(room1, room2, room3)),
                new HashSet<>()  // Inicialmente vacío
        );

        thematic2 = new Thematic(
                2L,
                "Test Temática 2",
                "Descripción de Test para la Temática 2",
                3245,
                true,
                user,
                new Date(),
                user,
                new Date(),
                new HashSet<>(Set.of(room4, room5, room6)),
                new HashSet<>()  // Inicialmente vacío
        );

        thematic3 = new Thematic(
                3L,
                "Test Temática 3",
                "Descripción de Test para la Temática 3",
                546,
                true,
                user,
                new Date(),
                user,
                new Date(),
                new HashSet<>(Set.of(room7)),
                new HashSet<>()  // Inicialmente vacío
        );

        // Ahora se crean los objetos DetailThematicComfort para la temática 1
        comfortDetail1 = new DetailThematicComfort(1L, user, new Date(), thematic1, comfort1);
        comfortDetail2 = new DetailThematicComfort(2L, user, new Date(), thematic1, comfort2);
        comfortDetail3 = new DetailThematicComfort(3L, user, new Date(), thematic1, comfort3);
        comfortDetail4 = new DetailThematicComfort(4L, user, new Date(), thematic1, comfort4);

        // Ahora se crean los objetos DetailThematicComfort para la temática 2
        comfortDetail5 = new DetailThematicComfort(5L, user, new Date(), thematic2, comfort3);
        comfortDetail6 = new DetailThematicComfort(6L, user, new Date(), thematic2, comfort4);
        comfortDetail7 = new DetailThematicComfort(7L, user, new Date(), thematic2, comfort5);
        comfortDetail8 = new DetailThematicComfort(8L, user, new Date(), thematic2, comfort6);
        comfortDetail9 = new DetailThematicComfort(9L, user, new Date(), thematic2, comfort7);
        comfortDetail10 = new DetailThematicComfort(10L, user, new Date(), thematic2, comfort8);
        comfortDetail11 = new DetailThematicComfort(11L, user, new Date(), thematic2, comfort9);

        // Ahora se crean los objetos DetailThematicComfort para la temática 3
        comfortDetail12 = new DetailThematicComfort(12L, user, new Date(), thematic3, comfort10);
        comfortDetail13 = new DetailThematicComfort(13L, user, new Date(), thematic3, comfort11);
        comfortDetail14 = new DetailThematicComfort(14L, user, new Date(), thematic3, comfort12);

        // Luego se añaden los detalles al objeto Thematic
        thematic1.setDetailThematicComforts(
                new HashSet<>(Set.of(
                        comfortDetail1,
                        comfortDetail2,
                        comfortDetail3,
                        comfortDetail4
                )));

        thematic2.setDetailThematicComforts(
                new HashSet<>(Set.of(
                        comfortDetail5,
                        comfortDetail6,
                        comfortDetail7,
                        comfortDetail8,
                        comfortDetail9,
                        comfortDetail10,
                        comfortDetail11
                )));

        thematic3.setDetailThematicComforts(
                new HashSet<>(Set.of(
                        comfortDetail12,
                        comfortDetail13,
                        comfortDetail4
                )));
    }

    public static CreateThematicDto createThematicDto() {
        return CreateThematicDto.builder()
                .name("Test Thematic")
                .description("Test Description")
                .comfortsListId(Arrays.asList(1L, 2L, 3L, 4L, 5L))
                .build();
    }

    public static List<Thematic> getMockThematics() {
        Thematic thematic1 = new Thematic();
        thematic1.setId(1L);
        thematic1.setName("Thematic 1");

        Thematic thematic2 = new Thematic();
        thematic2.setId(2L);
        thematic2.setName("Thematic 2");

        return List.of(thematic1, thematic2);
    }

}
