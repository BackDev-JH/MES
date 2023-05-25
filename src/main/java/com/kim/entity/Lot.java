package com.kim.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name="lottest2")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String weight;

    private String wash;

    private String extract;

    private String blend;

    private String packing;

    private String testing;

    private String cooling;

    private String boxing;

}
