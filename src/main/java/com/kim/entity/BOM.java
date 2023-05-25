package com.kim.entity;


import com.kim.dto.BOMdto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name="BOMtest2")
@ToString
public class BOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public static BOM createBOM(BOMdto bomDto){
        BOM bom = new BOM();
        bom.setProduct(bomDto.getProduct());
        bom.setCab(bomDto.getCab());
        bom.setGal(bomDto.getGal());
        bom.setPom(bomDto.getPom());
        bom.setPlu(bomDto.getPlu());
        bom.setCol(bomDto.getCol());
        bom.setWat(bomDto.getWat());
        bom.setPau(bomDto.getPau());
        bom.setBox(bomDto.getBox());
        bom.setStick(bomDto.getStick());


        return bom;
    }


}
