package com.kim.dto;

import com.kim.entity.Mat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class MatDto {

    private Long id;

    private String matName;

    private int matNum;


    private static ModelMapper modelMapper = new ModelMapper();

    public Mat createMat(){
        return modelMapper.map(this, Mat.class);
    }
    public static MatDto of(Mat mat) {
        return modelMapper.map(mat,MatDto.class);
    }



}
