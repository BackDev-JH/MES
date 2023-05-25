package com.kim.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LotDto {

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
