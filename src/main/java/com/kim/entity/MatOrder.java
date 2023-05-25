package com.kim.entity;


import com.kim.dto.MatOrderDto;
import com.kim.dto.OrdersDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.kim.constant.Status.PREORDER;

@Entity
@Getter
@Setter
@ToString
@Table(name="matOrdertest2")
public class MatOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matId;

    private LocalDateTime orderDate;

    private LocalDateTime comDate;

    private int leadTime;


    public static MatOrder createMatOrder(MatOrderDto matOrderDto){
        MatOrder matOrder = new MatOrder();
        matOrder.setMatId(matOrderDto.getMatId());
        matOrder.setOrderDate(matOrderDto.getOrderDate());
        matOrder.setComDate(matOrderDto.getComDate());
        matOrder.setLeadTime(matOrderDto.getLeadTime());

        return matOrder;
    }

}
