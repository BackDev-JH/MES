package com.kim.dto;

import com.kim.entity.PreProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class PreProductDto {

    private Long id;

    private String lotId;

    private String product;

    private int num;

    private static ModelMapper modelMapper = new ModelMapper();

    public PreProduct createPreproduct(){
        return modelMapper.map(this, PreProduct.class);
    }
    public static PreProductDto of(PreProduct preProduct) {
        return modelMapper.map(preProduct,PreProductDto.class);
    }
}
