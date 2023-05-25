package com.kim.entity;


import com.kim.dto.MoniteringDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name="moniteringtest2")
public class Monitering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lotId;

    private Long orderId;

    private Long productionId;

    private double advance;

    public static Monitering createMonitering(MoniteringDto moniteringDto){
        Monitering monitering = new Monitering();



        return monitering;
    }


}
