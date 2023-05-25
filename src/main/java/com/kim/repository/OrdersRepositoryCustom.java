package com.kim.repository;

import com.kim.dto.OrderSearchDto;
import com.kim.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepositoryCustom {

    Page<Orders> getOrdersPage(OrderSearchDto orderSearchDto, Pageable pageable);

}
