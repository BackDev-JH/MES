package com.kim.entity;

import com.kim.dto.OrdersDto;
import com.kim.dto.ProceDto;
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
@Table(name="procetest2")
public class Proce {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String lotId;

    private boolean status;

    private LocalDateTime endTime;

    private double amountHour;

    private int leadTime;

    private int maxAmount;

    public static Proce createProce(ProceDto proceDto){
        Proce proce = new Proce();
        proce.setName(proceDto.getName());
        proce.setLotId(proceDto.getLotId());
        proce.setStatus(proceDto.getStatus());
        proce.setEndTime(proceDto.getEndTime());
        proce.setAmountHour(proceDto.getAmountHour());
        proce.setLeadTime(proceDto.getLeadTime());
        proce.setMaxAmount(proceDto.getMaxAmount());
        return proce;
    }

    public void updateProce(ProceDto proceDto){
        this.endTime = proceDto.getEndTime();
    }

}
