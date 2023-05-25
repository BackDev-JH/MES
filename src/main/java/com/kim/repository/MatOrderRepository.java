package com.kim.repository;

import com.kim.entity.MatOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MatOrderRepository extends JpaRepository<MatOrder, Long>, QuerydslPredicateExecutor<MatOrder> {
}
