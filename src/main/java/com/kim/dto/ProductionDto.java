package com.kim.dto;

import com.kim.entity.Production;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class ProductionDto {

    private Long id;

    private Long orderId;

    private String lotId;

    private Long matId;

    private Long preId;

    private Long productId;

    private int matInput;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private static ModelMapper modelMapper = new ModelMapper();

    public Production createProduction(){
        return modelMapper.map(this, Production.class);
    }
    public static ProductionDto of(Production production) {
        return modelMapper.map(production,ProductionDto.class);
    }
}
