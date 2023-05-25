package com.kim.entity;

import com.kim.dto.OrdersDto;
import com.kim.dto.ProductionDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.kim.constant.Status.PREORDER;

@Entity
@Table(name="productiontest2")
@Getter
@Setter
@ToString
public class Production {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String lotId;

    private Long matId;

    private Long preId;

    private Long productId;

    private int matInput;

    private LocalDateTime startTime;

    private LocalDateTime endTime;


    public static Production createProduction(ProductionDto productionDto){
       Production production = new Production();
        production.setOrderId(productionDto.getOrderId());
        production.setLotId(productionDto.getLotId());
        production.setMatId(productionDto.getMatId());
        production.setPreId(productionDto.getPreId());
        production.setProductId(productionDto.getProductId());
        production.setMatInput(productionDto.getMatInput());
        production.setStartTime(productionDto.getStartTime());
        production.setEndTime(productionDto.getEndTime());
        return production;
    }
}
