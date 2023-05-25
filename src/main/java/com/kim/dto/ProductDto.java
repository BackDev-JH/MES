package com.kim.dto;

import com.kim.entity.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class ProductDto {

    private Long id;

    private String lotId;

    private String product;

    private int num;

    private static ModelMapper modelMapper = new ModelMapper();

    public Product createProduct(){
        return modelMapper.map(this, Product.class);
    }
    public static ProductDto of(Product product) {
        return modelMapper.map(product,ProductDto.class);
    }
}
