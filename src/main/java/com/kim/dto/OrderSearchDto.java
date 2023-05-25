
package com.kim.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchDto {

    private String searchBy;

    private String searchQuery = "";

    private String searchDateType;



}

