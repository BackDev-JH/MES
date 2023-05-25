package com.kim.repository;

import com.kim.dto.OrderSearchDto;
import com.kim.entity.Orders;
import com.kim.entity.QOrders;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class OrdersRepositoryCustomImpl implements OrdersRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public OrdersRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QOrders.orders.orderDate.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("id", searchBy)){
            return QOrders.orders.id.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("orderFrom", searchBy)){
            return QOrders.orders.orderFrom.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("product", searchBy)){
            return QOrders.orders.product.like("%" + searchQuery + "%");
        }/* else if(StringUtils.equals("orderDate", searchBy)){
            return QOrders.orders.orderDate.like("%" + searchQuery + "%");
        }*/

        return null;
    }
    @Override
    public Page<Orders> getOrdersPage(OrderSearchDto orderSearchDto, Pageable pageable) {
        QueryResults<Orders> results = queryFactory
                .selectFrom(QOrders.orders)
                .where(regDtsAfter(orderSearchDto.getSearchDateType()),
                        searchByLike(orderSearchDto.getSearchBy(),
                                orderSearchDto.getSearchQuery()))
                .orderBy(QOrders.orders.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Orders> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

}
