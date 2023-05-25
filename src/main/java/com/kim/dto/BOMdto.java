package com.kim.dto;

import com.kim.entity.BOM;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Setter
@Getter
@ToString

public class BOMdto {

    private Long id;

    private String product;


    private int cab;


    private int gal;


    private int pom;


    private int plu;


    private int col;


    private int wat;


    private int pau;


    private int box;

    private int stick;

    private static ModelMapper modelMapper = new ModelMapper();

    public BOM createBOM(){
        return modelMapper.map(this, BOM.class);
    }

    public static BOMdto of(BOM bom) {
        return modelMapper.map(bom,BOMdto.class);
    }


}
