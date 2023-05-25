package com.kim.dto;

import com.kim.constant.Status;
import com.kim.entity.Orders;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class OrdersDto {

    private Long id;

    private String orderFrom;

    private String product;

    private int box;

    private LocalDateTime orderDate;

    private LocalDateTime comDate;

    private Status status;


    private static ModelMapper modelMapper = new ModelMapper();

    public Orders createOrder(){
        return modelMapper.map(this, Orders.class);
    }

    public static OrdersDto of(Orders order) {
        return modelMapper.map(order, OrdersDto.class);
    }



}
