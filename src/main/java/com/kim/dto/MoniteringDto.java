package com.kim.dto;

import com.kim.entity.Monitering;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Setter
@Getter
@ToString
public class MoniteringDto {

    private Long id;

    private String lotId;

    private Long orderId;

    private Long productionId;

    private double advance;

    private static ModelMapper modelMapper = new ModelMapper();

    public Monitering createMonitering(){
        return modelMapper.map(this, Monitering.class);
    }

    public static MoniteringDto of(Monitering monitering) {
        return modelMapper.map(monitering, MoniteringDto.class);
    }

}
