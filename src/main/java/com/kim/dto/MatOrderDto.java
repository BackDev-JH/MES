package com.kim.dto;

import com.kim.entity.MatOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class MatOrderDto {

    private Long id;

    private String matId;

    private LocalDateTime orderDate;

    private LocalDateTime comDate;

    private int leadTime;

    private static ModelMapper modelMapper = new ModelMapper();


    public MatOrder createMatOrder(){
        return modelMapper.map(this, MatOrder.class);
    }
    public static MatOrderDto of(MatOrder matOrder) {
        return modelMapper.map(matOrder,MatOrderDto.class);
    }
}
