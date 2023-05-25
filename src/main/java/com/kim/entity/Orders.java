package com.kim.entity;

import com.kim.constant.Status;
import com.kim.dto.OrdersDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.kim.constant.Status.PREORDER;

@Entity
@Table(name="orderstest2")
@Setter
@Getter
@ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderFrom;

    private String product;

    private int box;

    private LocalDateTime orderDate;

    private LocalDateTime comDate;

    private Status status;



    public static Orders createOrder(OrdersDto ordersDto){
        Orders orders = new Orders();
        orders.setOrderFrom(ordersDto.getOrderFrom());
        orders.setProduct(ordersDto.getProduct());
        orders.setBox(ordersDto.getBox());
        orders.setOrderDate(LocalDateTime.now());
        orders.setStatus(PREORDER);
        return orders;
    }

    public void updateComDate(LocalDateTime comDate){
        this.comDate = comDate;
    }

    public void updateOrders(OrdersDto ordersDto){
        this.orderFrom = ordersDto.getOrderFrom();
        this.product = ordersDto.getProduct();
        this.box = ordersDto.getBox();
        this.orderDate = LocalDateTime.now();
        this.status = ordersDto.getStatus();

    }
    public void updateOrders2(OrdersDto ordersDto){
        this.orderFrom = ordersDto.getOrderFrom();
        this.product = ordersDto.getProduct();
        this.box = ordersDto.getBox();
        this.status = ordersDto.getStatus();

    }
}
