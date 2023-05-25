package com.kim.dto;

import com.kim.entity.Proce;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;



@Getter
@Setter
@ToString
public class ProceDto {

    private Long id;

    private String name;

    private String lotId;

    private boolean status;

    private LocalDateTime endTime;

    private double amountHour;

    private int leadTime;

    private int maxAmount;

    private static ModelMapper modelMapper = new ModelMapper();

    public Proce createProce(){
        return modelMapper.map(this, Proce.class);
    }

    public static ProceDto of(Proce proce) {
        return modelMapper.map(proce, ProceDto.class);
    }

    public boolean getStatus(){
        return this.status;
    }
}
