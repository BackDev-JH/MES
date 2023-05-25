package com.kim.entity;

import com.kim.dto.MatDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name="mattest2")
public class Mat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matName;

    private int matNum;


    public static Mat createMat(MatDto matDto){
        Mat mat = new Mat();
        mat.setMatName(matDto.getMatName());
        mat.setMatNum(matDto.getMatNum());

        return mat;
    }


}
